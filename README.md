# Gestión de Usuarios — Desafío Técnico Java + ZK

Aplicación web para el **CRUD de usuarios** (Crear, Leer, Actualizar, Eliminar) construida con **Java**, el framework **ZK** para el frontend, **PostgreSQL** como base de datos y desplegable en **Tomcat**.

Toda la lógica de negocio (creación, modificación, eliminación, validación y autenticación de usuarios) vive en una **librería independiente** (`user-authenticator-core`), empaquetada como JAR y reutilizable por otros proyectos.

---

## Arquitectura

El proyecto está dividido en **dos módulos Maven independientes**:

```
user_management/
├── user-authenticator-core/     → Librería (JAR) con la lógica de usuarios
│   └── com.river.challenge
│       ├── model/User.java              → Entidad (Modelo)
│       ├── dao/UserDao.java             → Persistencia (JDBC + PreparedStatement)
│       ├── service/UserService.java     → Fachada de negocio (validación + orquestación)
│       ├── security/PasswordEncoder.java→ Cifrado BCrypt
│       ├── auth/Authenticator.java      → Clase abstracta de autenticación
│       ├── auth/UserAuthenticatorImpl.java → Implementación concreta
│       └── config/DatabaseConnection.java  → Conexión configurable
│
└── user-management-web/         → Aplicación web (WAR) con ZK
    └── com.river.challenge.controller
        └── UserController.java          → Controlador (MVC)
    └── webapp
        └── index.zul                    → Vista (ZK): componentes + estilos incrustados
```

### Patrón MVC (capa web)
- **Modelo**: `User` (en la librería).
- **Vista**: `index.zul` — todo el frontend (componentes ZK y estilos CSS incrustados en un `<style>`) vive en este único archivo.
- **Controlador**: `UserController` (`SelectorComposer` de ZK) que delega en `UserService`.

### La librería como componente independiente
- Es un módulo Maven aparte (`packaging=jar`), **no embebido** en el proyecto web; este la consume como dependencia.
- Expone la clase abstracta **`Authenticator`** con `authenticate(login, password)`, extendida por `UserAuthenticatorImpl`, para que **otros proyectos** puedan autenticar usuarios contra el mismo repositorio.

---

## Seguridad

- **Inyección SQL**: todas las consultas usan `PreparedStatement` con parámetros; no hay concatenación de cadenas SQL.
- **XSS**: ZK escapa por defecto el contenido de los componentes (`Label`, `Listcell`), evitando la inyección de HTML/JS.
- **Contraseñas**: se almacenan **cifradas con BCrypt** (salt aleatorio, coste 12). Nunca en texto plano. La autenticación verifica el hash.
- **Credenciales de BD**: externalizadas en `db.properties` y sobreescribibles por variables de entorno (`DB_URL`, `DB_USER`, `DB_PASSWORD`).

---

## Requisitos previos

- JDK 11+
- Maven 3.6+
- PostgreSQL 12+
- Apache Tomcat 9 (usa `javax.servlet`, compatible con ZK 9.6)

---

## Puesta en marcha

### 1. Base de datos

```sql
CREATE DATABASE user_management;
```

Luego, conectado a `user_management`, ejecuta el script [`schema.sql`](./schema.sql).

### 2. Configuración de conexión

Edita `user-authenticator-core/src/main/resources/db.properties` (o define variables de entorno):

```properties
db.url=jdbc:postgresql://localhost:5432/user_management
db.user=postgres
db.password=admin123
```

### 3. Compilación

Primero la librería (se instala en el repositorio local de Maven), luego la web:

```bash
cd user-authenticator-core && mvn clean install
cd ../user-management-web && mvn clean package
```

Esto genera `user-management-web/target/user-management-web.war`.

### 4. Despliegue en Tomcat

```bash
cp user-management-web/target/user-management-web.war $CATALINA_HOME/webapps/
```

Tomcat lo desplegará automáticamente. Abre en el navegador:

```
http://localhost:8080/user-management-web/
```

---

## Pruebas

La librería incluye pruebas unitarias (JUnit 4) de la validación de negocio y del cifrado:

```bash
cd user-authenticator-core && mvn test
```

---

## Funcionalidades

- Crear, listar, editar y eliminar usuarios.
- Validación de formularios (usuario, email, contraseña).
- Borrado lógico (`activo = false`), preservando el historial.
- Al editar, la contraseña puede dejarse en blanco para conservarla.
- Interfaz responsive con paginación y ordenamiento de columnas.
