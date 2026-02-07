// Qui inseriamo la logica di validazione del token di Google.

package com.shulehub.backend.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.shulehub.backend.entity.User;
import com.shulehub.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    // Inserisci il Client ID che hai in index.html
    private final String CLIENT_ID = "651622332732-hqg898c50786ii5rpa4iieo43gb6kmc8.apps.googleusercontent.com";

    public User verifyGoogleToken(String idTokenString) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();

            // Cerchiamo l'utente nel DB Supabase
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utente non censito nel sistema: " + email));
        } else {
            throw new RuntimeException("Token Google non valido");
        }
    }
}
