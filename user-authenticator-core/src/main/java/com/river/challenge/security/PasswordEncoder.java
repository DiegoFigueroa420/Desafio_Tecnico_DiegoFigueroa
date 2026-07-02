package com.river.challenge.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Cifrado y verificación de contraseñas usando BCrypt.
 * Evita almacenar contraseñas en texto plano.
 */
public class PasswordEncoder {

    /** Coste de trabajo del algoritmo (2^cost iteraciones). */
    private static final int COST = 12;

    /**
     * Genera un hash BCrypt (con salt aleatorio incorporado) de la contraseña.
     */
    public String hash(String plainPassword) {
        if (plainPassword == null) {
            throw new IllegalArgumentException("La contraseña no puede ser nula");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(COST));
    }

    /**
     * Verifica una contraseña en texto plano contra su hash almacenado.
     */
    public boolean matches(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // El hash almacenado no tiene formato BCrypt válido (p. ej. datos antiguos en texto plano)
            return false;
        }
    }
}
