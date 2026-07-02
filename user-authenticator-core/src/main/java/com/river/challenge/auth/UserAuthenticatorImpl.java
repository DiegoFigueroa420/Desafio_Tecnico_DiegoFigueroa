package com.river.challenge.auth;

import com.river.challenge.config.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAuthenticatorImpl extends Authenticator {

    @Override
    public boolean authenticate(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT id FROM usuarios WHERE (username = ? OR email = ?) AND password = ? AND activo = true";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, username);
            pstmt.setString(3, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error en autenticación: " + e.getMessage());
            return false;
        }
    }

    public String validateUserForm(String username, String email, String password) {
        if (username == null || username.trim().isEmpty()) return "El nombre de usuario es obligatorio.";
        if (email == null || email.trim().isEmpty()) return "El correo electrónico es obligatorio.";
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) return "El formato del correo electrónico no es válido.";
        if (password == null || password.trim().isEmpty()) return "La contraseña es obligatoria.";
        if (password.length() < 4) return "La contraseña debe tener al menos 4 caracteres.";
        return null;
    }
}
