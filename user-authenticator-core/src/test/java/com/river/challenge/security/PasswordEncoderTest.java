package com.river.challenge.security;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class PasswordEncoderTest {

    private final PasswordEncoder encoder = new PasswordEncoder();

    @Test
    public void hashShouldDifferFromPlainText() {
        String hash = encoder.hash("secreta123");
        assertNotEquals("secreta123", hash);
        assertTrue("El hash debe tener prefijo BCrypt", hash.startsWith("$2"));
    }

    @Test
    public void matchesShouldReturnTrueForCorrectPassword() {
        String hash = encoder.hash("secreta123");
        assertTrue(encoder.matches("secreta123", hash));
    }

    @Test
    public void matchesShouldReturnFalseForWrongPassword() {
        String hash = encoder.hash("secreta123");
        assertFalse(encoder.matches("otraClave", hash));
    }

    @Test
    public void matchesShouldReturnFalseForNullsOrPlainTextHash() {
        assertFalse(encoder.matches(null, "hash"));
        assertFalse(encoder.matches("clave", null));
        assertFalse(encoder.matches("clave", "no-es-un-hash-bcrypt"));
    }

    @Test
    public void differentHashesForSamePassword() {
        // Gracias al salt aleatorio, dos hashes de la misma clave deben diferir
        assertNotEquals(encoder.hash("misma"), encoder.hash("misma"));
    }
}
