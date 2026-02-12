package com.shulehub.backend.auth.service;

// --- Spring Framework ---
import org.springframework.stereotype.Service;

// --- Google OAuth ---
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

// --- Model: Entities & DTOs ---
import com.shulehub.backend.auth.model.entity.EmployeeProfileView;
import com.shulehub.backend.school_config.model.entity.Year;
import com.shulehub.backend.auth.model.dto.UserAuthDTO;
import com.shulehub.backend.auth.model.dto.TeacherContextDTO;
import com.shulehub.backend.teacher_assignment.model.dto.TeacherAssignmentDTO;

// --- Repositories ---
import com.shulehub.backend.auth.repository.EmployeeProfileViewRepository;
import com.shulehub.backend.auth.repository.PermissionRepository;
import com.shulehub.backend.school_config.repository.YearRepository;
import com.shulehub.backend.teacher_assignment.repository.TeacherAssignmentRepository;

// --- Mappers & Exceptions ---
import com.shulehub.backend.auth.mapper.AuthMapper;
import com.shulehub.backend.common.exception.UnauthorizedException;

// --- Java Utils ---
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final EmployeeProfileViewRepository profileViewRepository;
    private final PermissionRepository permissionRepository;
    private final YearRepository yearRepository;
    private final TeacherAssignmentRepository teacherAssignmentRepository;
    private final AuthMapper authMapper;

    // Sostituisci con il tuo Client ID reale
    private final String CLIENT_ID = "651622332732-hqg898c50786ii5rpa4iieo43gb6kmc8.apps.googleusercontent.com";

    public AuthServiceImpl(EmployeeProfileViewRepository profileViewRepository,
                           PermissionRepository permissionRepository,
                           YearRepository yearRepository,
                           TeacherAssignmentRepository teacherAssignmentRepository,
                           AuthMapper authMapper) {
        this.profileViewRepository = profileViewRepository;
        this.permissionRepository = permissionRepository;
        this.yearRepository = yearRepository;
        this.teacherAssignmentRepository = teacherAssignmentRepository;
        this.authMapper = authMapper;
    }

    @Override
    public UserAuthDTO loginWithGoogle(String email) {
        // 1. Recupero il profilo utente dalla Vista filtrando per email
        EmployeeProfileView view = profileViewRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Accesso negato: utente non censito o non attivo"));

        // 2. Converto l'Entity in DTO base tramite il Mapper
        UserAuthDTO authDto = authMapper.toAuthDTO(view);

        // 3. Recupero i permessi nominali (es: "VOTE_INSERT")
        Set<String> permissions = permissionRepository.findNamesByProfileId(view.getProfileId());
        authDto.setPermissions(permissions);

        // 4. Se l'utente è un docente, arricchisco con il contesto operativo
        if ("TEACHER".equalsIgnoreCase(view.getProfileName())) {
            
            // Cerco l'anno scolastico attivo
            Year activeYear = yearRepository.findByYearIsActiveTrue()
                    .orElseThrow(() -> new RuntimeException("Configurazione mancante: nessun anno scolastico attivo"));

            // Recupero le assegnazioni (Classi + Materie)
            List<TeacherAssignmentDTO> assignments = teacherAssignmentRepository
                    .findTeacherContext(view.getEmployeeId(), activeYear.getId());

            TeacherContextDTO teacherCtx = new TeacherContextDTO();
            teacherCtx.setAssignments(assignments);

            // Identifico gli ID delle classi di cui è coordinatore
            Set<Integer> classTeacherRoomIds = assignments.stream()
                    .filter(TeacherAssignmentDTO::isClassTeacher)
                    .map(TeacherAssignmentDTO::getYearRoomId)
                    .collect(Collectors.toSet());
            
            teacherCtx.setClassTeacherRoomIds(classTeacherRoomIds);
            
            // Collego il contesto docente al DTO principale
            authDto.setTeacherContext(teacherCtx);
        }

        return authDto;
    }

    @Override
    public void verifyGoogleToken(String idTokenString) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .setAcceptableTimeSkewSeconds(30) // <--- aggiunge tolleranza fra orologio render e orologio Google
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken == null) {
            throw new UnauthorizedException("Token Google non valido o scaduto");
        }
    }
}