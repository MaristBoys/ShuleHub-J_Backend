package com.shulehub.backend.auth.service;

import com.shulehub.backend.auth.model.dto.UserAuthDTO;

public interface AuthService {
    // Questo è il metodo che il tuo AuthController sta cercando di chiamare
    UserAuthDTO loginWithGoogle(String email, String pictureUrl);
    
    // Se ti serve ancora il metodo per verificare il token, dichiaralo qui 
    // ma la logica andrà nell'impl
    void verifyGoogleToken(String idTokenString) throws Exception;
}
