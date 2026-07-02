package com.river.challenge.auth;

import com.river.challenge.dao.UserDao;
import com.river.challenge.model.User;
import com.river.challenge.security.PasswordEncoder;

/**
 * Implementación concreta de {@link Authenticator}.
 *
 * Verifica las credenciales contra la contraseña cifrada (BCrypt) almacenada
 * en la base de datos. Puede ser reutilizada por otros proyectos para autenticar
 * usuarios contra el mismo repositorio.
 */
public class UserAuthenticatorImpl extends Authenticator {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public UserAuthenticatorImpl() {
        this(new UserDao(), new PasswordEncoder());
    }

    public UserAuthenticatorImpl(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Autentica por username o email + contraseña.
     * @return true si las credenciales son válidas y el usuario está activo.
     */
    @Override
    public boolean authenticate(String login, String password) {
        if (login == null || login.trim().isEmpty() || password == null || password.isEmpty()) {
            return false;
        }
        User user = userDao.findByUsernameOrEmail(login.trim());
        return user != null && passwordEncoder.matches(password, user.getPassword());
    }
}
