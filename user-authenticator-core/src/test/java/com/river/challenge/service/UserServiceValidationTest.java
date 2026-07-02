package com.river.challenge.service;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Pruebas de la validación de negocio (no requieren base de datos).
 */
public class UserServiceValidationTest {

    private final UserService service = new UserService();

    @Test
    public void validDataReturnsNull() {
        assertNull(service.validate("alejandro", "ale@correo.com", "clave1234", true));
    }

    @Test
    public void emptyUsernameIsRejected() {
        assertNotNull(service.validate("  ", "ale@correo.com", "clave1234", true));
    }

    @Test
    public void shortUsernameIsRejected() {
        assertNotNull(service.validate("al", "ale@correo.com", "clave1234", true));
    }

    @Test
    public void invalidEmailIsRejected() {
        assertNotNull(service.validate("alejandro", "correo-invalido", "clave1234", true));
    }

    @Test
    public void shortPasswordIsRejected() {
        assertNotNull(service.validate("alejandro", "ale@correo.com", "123", true));
    }

    @Test
    public void passwordOptionalOnEditWhenBlank() {
        // Al editar (passwordRequired=false) una contraseña vacía es válida
        assertNull(service.validate("alejandro", "ale@correo.com", "", false));
    }

    @Test
    public void passwordValidatedOnEditWhenProvided() {
        // Si al editar se escribe una contraseña, debe cumplir la longitud mínima
        assertNotNull(service.validate("alejandro", "ale@correo.com", "12", false));
    }
}
