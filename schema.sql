-- ============================================================
-- Esquema de base de datos - Gestión de Usuarios (River Software)
-- Motor: PostgreSQL
-- ============================================================

-- 1) Crear la base de datos (ejecutar conectado a la BD "postgres")
--    CREATE DATABASE user_management;

-- 2) Conectarse a user_management y crear la tabla:

CREATE TABLE IF NOT EXISTS usuarios (
    id       SERIAL PRIMARY KEY,
    username VARCHAR(50)  UNIQUE NOT NULL,
    email    VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,          -- almacena el hash BCrypt (~60 caracteres)
    activo   BOOLEAN DEFAULT TRUE
);

-- Índices útiles para la autenticación y el listado
CREATE INDEX IF NOT EXISTS idx_usuarios_activo ON usuarios (activo);

-- Nota de seguridad:
-- Las contraseñas se guardan cifradas con BCrypt desde la aplicación.
-- No inserte contraseñas en texto plano manualmente; créelas desde la interfaz web.
