# 🍓 Frutti Backend

Este proyecto corresponde al backend de la aplicación **Frutti**, un sistema desarrollado en Java con el framework Spring Boot, que permite a los usuarios analizar la frescura de frutas mediante inteligencia artificial. El backend proporciona servicios REST para la gestión de usuarios, autenticación y manejo de historial de frutas, y está actualmente desplegado en una instancia **EC2** de AWS con conexión a una base de datos **MySQL en RDS**.

## 📦 Tecnologías utilizadas

- Java 17
- Spring Boot 3
- Spring Security + JWT
- JPA + Hibernate
- MySQL (en Amazon RDS)
- AWS EC2 + RDS
- JUnit 5 + Mockito (pruebas unitarias)
- SpringBootTest + MockMvc (pruebas de integración)

## 🧩 Estructura general

El proyecto sigue una arquitectura por capas:

- **Controladores (`controller`)**: gestionan las peticiones HTTP.
  - `/auth` → login
  - `/usuario` → CRUD de usuarios y manejo de credenciales
  - `/fruta` → Registro, consulta y gestión de frutas analizadas

- **Servicios (`service`)**: lógica de negocio para cada entidad.
- **Repositorios (`repository`)**: comunicación con la base de datos (JPA).
- **DTOs (`DTOs`)**: objetos para transferencia de datos entre capas.
- **Configuración (`config`)**: configuración de seguridad y excepciones globales.

## 🚀 Endpoints principales

### Autenticación
- `POST /auth/login`: Inicio de sesión, genera token JWT

### Usuario
- `POST /usuario/registrarUsuario`: Registrar nuevo usuario
- `GET /usuario/listarUsuarios`: Listar todos los usuarios
- `GET /usuario/obtenerUsuario/{email}`: Obtener usuario por email
- `GET /usuario/obtenerid/{email}`: Obtener ID por email
- `PATCH /usuario/actualizarUsuario/{id}`: Actualizar usuario
- `PATCH /usuario/actualizarContraseña/{id}`: Cambiar contraseña
- `DELETE /usuario/eliminar/{id}`: Eliminar usuario

### Fruta
- `POST /fruta/registrarFruta`: Registrar análisis de fruta
- `GET /fruta/listarFrutas`: Listar mejores frutas
- `GET /fruta/historial/{idUsuario}`: Obtener historial de frutas por usuario
- `GET /fruta/obtenerFruta/{idFruta}, {idUsuario}`: Obtener fruta específica
- `DELETE /fruta/eliminar/{idFruta}/{idUsuario}`: Eliminar fruta
- `DELETE /fruta/eliminarHistorial/{idUsuario}`: Eliminar todo el historial

## ⚙️ Ejecución local

Para ejecutar el proyecto localmente, sigue estos pasos:

1. Clona el repositorio.
2. Asegúrate de tener una base de datos MySQL en ejecución.
3. Modifica el archivo `src/main/resources/application.properties` con tus credenciales locales:
   
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/frutti_db
    spring.datasource.username=tu_usuario
    spring.datasource.password=tu_contraseña
    ```
5. Ejecuta el proyecto con:
    ```bash
    ./mvnw spring-boot:run
    ```
    o desde tu IDE con la clase `FruttiApplication.java`.

> **Nota**: actualmente el backend está desplegado y funcionando en una instancia EC2, por lo que no es necesario ejecutarlo localmente a menos que desees realizar pruebas o desarrollo adicional.

## 🧪 Pruebas

El backend ha sido validado mediante:
- ✅ Pruebas unitarias para entidades `Usuario` y `Fruta`
- ✅ Pruebas de integración para todos los controladores
- ✅ Pruebas de rendimiento con JMeter (registro, listado y consulta de frutas)
- ✅ Pruebas de aceptación de usuario y adaptabilidad en múltiples dispositivos
