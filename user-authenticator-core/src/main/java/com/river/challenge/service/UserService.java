package com.river.challenge.service;

import com.river.challenge.dao.UserDao;
import com.river.challenge.model.User;
import com.river.challenge.security.PasswordEncoder;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Fachada de negocio de la librería de manejo de usuarios.
 *
 * Centraliza la validación y orquesta el cifrado de contraseñas + la persistencia.
 * Es el único punto de entrada que necesita conocer la capa web.
 */
public class UserService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public UserService() {
        this(new UserDao(), new PasswordEncoder());
    }

    /** Constructor para inyección de dependencias (facilita las pruebas). */
    public UserService(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> listActiveUsers() {
        return userDao.findAll();
    }

    /**
     * Valida los datos del formulario.
     *
     * @param passwordRequired si la contraseña es obligatoria (true al crear;
     *                         al editar puede dejarse vacía para no cambiarla).
     * @return mensaje de error, o null si los datos son válidos.
     */
    public String validate(String username, String email, String password, boolean passwordRequired) {
        if (isBlank(username)) {
            return "El nombre de usuario es obligatorio.";
        }
        if (username.trim().length() < 3) {
            return "El nombre de usuario debe tener al menos 3 caracteres.";
        }
        if (isBlank(email)) {
            return "El correo electrónico es obligatorio.";
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return "El formato del correo electrónico no es válido.";
        }
        if (passwordRequired || !isBlank(password)) {
            if (isBlank(password)) {
                return "La contraseña es obligatoria.";
            }
            if (password.length() < 4) {
                return "La contraseña debe tener al menos 4 caracteres.";
            }
        }
        return null;
    }

    /** Crea un usuario nuevo cifrando su contraseña. */
    public boolean createUser(User user) {
        user.setUsername(user.getUsername().trim());
        user.setEmail(user.getEmail().trim());
        user.setPassword(passwordEncoder.hash(user.getPassword()));
        return userDao.create(user);
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param changePassword si true, cifra y actualiza la contraseña incluida en {@code user};
     *                       si false, conserva la contraseña actual en la base de datos.
     */
    public boolean updateUser(User user, boolean changePassword) {
        user.setUsername(user.getUsername().trim());
        user.setEmail(user.getEmail().trim());
        if (changePassword) {
            user.setPassword(passwordEncoder.hash(user.getPassword()));
            return userDao.update(user);
        }
        return userDao.updateProfile(user);
    }

    public boolean deleteUser(int id) {
        return userDao.delete(id);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
