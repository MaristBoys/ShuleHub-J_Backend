package com.shulehub.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles; // Importante!

@SpringBootTest
@ActiveProfiles("test") // Questo attiva il file application-test.properties
class BackendApplicationTests {

    @Test
    void contextLoads() {
        // Questo test ora passerà perché Spring troverà il database H2 in memoria
        // invece di bloccarsi cercando PostgreSQL o variabili mancanti.
    }

}