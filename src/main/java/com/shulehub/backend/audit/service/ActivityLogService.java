// Qui ho inserito la logica specifica per estrarre l'IP dall'header X-Forwarded-For di Render.

package com.shulehub.backend.audit.service;

import com.shulehub.backend.audit.model.entity.ActivityLog;
import com.shulehub.backend.audit.repository.ActivityLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository repository;

    @Transactional
    public void log(String identifier, UUID userId, String actionType, String description, 
                HttpServletRequest request, Map<String, Object> extraData) {
    
        String ipAddress = extractIp(request);
        String userAgent = request.getHeader("User-Agent"); // Es: "Mozilla/5.0 (Windows NT 10.0...)"
        String acceptLanguage = request.getHeader("Accept-Language"); // Es: "it-IT,it;q=0.9"

        // Arricchiamo extraData con la lingua se non è già presente
        Map<String, Object> data = (extraData != null) ? extraData : new HashMap<>();
        if (acceptLanguage != null) {
            data.put("browser_lang", acceptLanguage);
        }

        ActivityLog entry = ActivityLog.builder()
                .identifier(identifier)
                .userId(userId)
                .actionType(actionType)
                .description(description)
                .ipAddress(ipAddress)
                .userAgent(userAgent) // Salviamo la stringa completa nel campo dedicato
                .extra_data(data)
                .build();

        repository.save(entry);
    }


    private String extractIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // Prende il primo IP della lista (quello del client reale)
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}