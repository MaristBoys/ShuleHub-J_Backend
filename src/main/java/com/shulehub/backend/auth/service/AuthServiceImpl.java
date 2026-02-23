package com.shulehub.backend.auth.service;

// --- Spring Framework ---
import org.springframework.stereotype.Service;

// --- Google OAuth ---
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

// --- Model: Entities & DTOs ---
import com.shulehub.backend.auth.model.entity.User;
import com.shulehub.backend.registry.model.entity.Employee;
import com.shulehub.backend.school_config.model.entity.Year;
import com.shulehub.backend.auth.model.dto.UserAuthDTO;
import com.shulehub.backend.auth.model.dto.TeacherContextDTO;
import com.shulehub.backend.teacher_assignment.model.dto.TeacherAssignmentDTO;


// --- Repositories ---
import com.shulehub.backend.auth.repository.UserRepository;
import com.shulehub.backend.registry.repository.EmployeeRepository;
import com.shulehub.backend.auth.repository.PermissionRepository;
import com.shulehub.backend.school_config.repository.YearRepository;
import com.shulehub.backend.teacher_assignment.repository.TeacherAssignmentRepository;

// --- Exceptions ---
import com.shulehub.backend.common.exception.UnauthorizedException;

// --- Java Utils ---
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    // Dichiarazione di tutti i repository usati nel codice
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PermissionRepository permissionRepository;
    private final YearRepository yearRepository;
    private final TeacherAssignmentRepository teacherAssignmentRepository;

    // Sostituisci con il tuo Client ID reale
    private final String CLIENT_ID = "651622332732-hqg898c50786ii5rpa4iieo43gb6kmc8.apps.googleusercontent.com";

    // Costruttore aggiornato con tutte le dipendenze
    public AuthServiceImpl(UserRepository userRepository,
                           EmployeeRepository employeeRepository,
                           PermissionRepository permissionRepository,
                           YearRepository yearRepository,
                           TeacherAssignmentRepository teacherAssignmentRepository) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.permissionRepository = permissionRepository;
        this.yearRepository = yearRepository;
        this.teacherAssignmentRepository = teacherAssignmentRepository;
    }

    @Override
    public UserAuthDTO loginWithGoogle(String email, String pictureUrl) {
        // 1. Recupero dell'utente tramite email
        // Se l'email non esiste, lanciamo un'eccezione che verrà catturata dal GlobalExceptionHandler
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UnauthorizedException("Accesso negato: email non censita nel sistema."));

        // 2. Controllo stato utente (Active/Disabled)
        if (!user.isUserIsActive()) {
            throw new UnauthorizedException("Account disabled");
        }

        // 3. Creazione e popolazione del DTO
        UserAuthDTO authDto = new UserAuthDTO();
        authDto.setUserId(user.getId());
        authDto.setEmail(user.getEmail());
        authDto.setUsername(user.getUsername());
        authDto.setProfileName(user.getProfile().getProfileName());
        authDto.setProfileId(user.getProfile().getId()); // Short recuperato tramite associazione ORM
        authDto.setPictureUrl(pictureUrl); // Popoliamo la foto che arriva da Google

        // 4. Recupero permessi tramite Query JPQL esplicita
        Set<String> permissions = permissionRepository.findCodesByProfileId(user.getProfile().getId());
        authDto.setPermissions(permissions);

        // 5. Gestione Dinamica del Contesto Docente (Indipendente dal Profilo)
        // Cerchiamo se l'utente è censito come Employee (Impiegato)
        employeeRepository.findByUserId(user.getId()).ifPresent(employee -> {
    
            // Se l'impiegato non è attivo, non carichiamo il contesto (opzionale: potresti lanciare eccezione)
            if (!employee.isEmployeeIsActive()) return;

            // Recupero l'anno attivo per controllare le assegnazioni attuali
            Year activeYear = yearRepository.findByYearIsActiveTrue()
                .orElseThrow(() -> new RuntimeException("Configurazione mancante: nessun anno scolastico attivo"));

            // Recupero le assegnazioni (Classi/Materie)
            List<TeacherAssignmentDTO> assignments = teacherAssignmentRepository
                .findTeacherContext(employee.getId(), activeYear.getId());

            // SE l'impiegato ha effettivamente delle classi assegnate (è un docente "di fatto")
            if (!assignments.isEmpty()) {
                TeacherContextDTO teacherCtx = new TeacherContextDTO();
                teacherCtx.setAssignments(assignments);

                // Identifico dove è coordinatore
                Set<Integer> classTeacherRoomIds = assignments.stream()
                    .filter(TeacherAssignmentDTO::isClassTeacher)   // "Prendi ogni oggetto della lista, chiama il metodo getter isClassTeacher() che Lombok ha creato per me, e se restituisce true tienilo".
                    .map(TeacherAssignmentDTO::getYearRoomId)       // "mappare" significa trasformare. "Per ogni oggetto che è sopravvissuto al filtro, prendi il suo ID della classe e passalo allo step successivo"
                                                                //lo Stream passa da essere uno Stream<TeacherAssignmentDTO> a uno Stream<Integer>
                    .collect(Collectors.toSet());                   // .collect() dice allo Stream di fermarsi e raccogliere tutto ciò che è rimasto.
                                                                // Collectors.toSet() specifica che vogliamo i risultati dentro un Set (set no duplicati)

                teacherCtx.setClassTeacherRoomIds(classTeacherRoomIds);

                // Collego il contesto al DTO
                authDto.setTeacherContext(teacherCtx);

                // --- IL TOCCO DI CLASSE ---
                // Aggiungiamo dinamicamente il permesso di vedere la parte "Teacher" 
                // anche se il suo profilo primario è un altro (es. SECRETARY)
                authDto.getPermissions().add("ACCESS_TEACHER_AREA");
            }   
        });
    
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