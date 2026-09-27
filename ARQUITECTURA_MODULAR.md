# Arquitectura modular de FitTrack

## Objetivo

Organizar el backend como un **monolito modular por funcionalidad**, manteniendo un único proyecto Maven y una aplicación Spring Boot. Cada módulo agrupa todo lo relacionado con una capacidad de negocio, en lugar de repartir las clases globalmente entre `controller`, `service`, `entity` y `repository`.

Esta estructura facilita localizar código, reducir dependencias accidentales y permitir una futura separación en módulos Maven si el proyecto crece.

## Estructura propuesta

```text
src/main/java/com/fittrack/
├── FitTrackApplication.java
├── shared/
│   ├── exception/
│   └── response/
├── config/
│   └── SecurityConfig.java
├── security/
│   ├── ApplicationConfig.java
│   ├── JwtAuthenticationFilter.java
│   └── JwtService.java
├── auth/
│   ├── controller/
│   │   └── AuthController.java
│   ├── dto/
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   └── AuthResponse.java
│   └── service/
│       └── AuthService.java
├── usuario/
│   ├── controller/
│   │   └── UsuarioController.java
│   ├── dto/
│   ├── entity/
│   │   └── Usuario.java
│   ├── repository/
│   │   └── UsuarioRepository.java
│   └── service/
│       └── UsuarioService.java
├── ejercicio/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   │   └── Ejercicio.java
│   ├── repository/
│   └── service/
├── rutina/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   │   ├── Rutina.java
│   │   └── RutinaEjercicio.java
│   ├── repository/
│   ├── service/
│   └── enums/
│       └── DiaSemana.java
└── entrenamiento/
    ├── controller/
    ├── dto/
    ├── entity/
    │   ├── Entrenamiento.java
    │   └── RegistroSerie.java
    ├── repository/
    └── service/
```

## Responsabilidad de cada módulo

### `auth`

Gestiona el registro, el inicio de sesión y las respuestas de autenticación.

Debe contener:

- `AuthController`.
- `AuthService`.
- DTOs de login, registro y respuesta JWT.

Puede utilizar el repositorio y la entidad de `usuario`, pero no debe duplicar la lógica de usuarios.

### `usuario`

Gestiona la información y las operaciones propias del usuario.

Debe contener:

- `Usuario`.
- `UsuarioRepository`.
- `UsuarioService`.
- `UsuarioController`.
- DTOs para entrada y salida de datos del usuario.

### `ejercicio`

Gestiona los ejercicios disponibles y los ejercicios creados por cada usuario.

### `rutina`

Gestiona las rutinas y su planificación.

`RutinaEjercicio` debe permanecer dentro de este módulo porque representa la relación entre una rutina y los ejercicios que la componen.

`DiaSemana` también puede permanecer dentro de `rutina` mientras no sea utilizado por otros módulos.

### `entrenamiento`

Gestiona las sesiones de entrenamiento realizadas.

`RegistroSerie` debe permanecer dentro de este módulo porque representa el detalle de una sesión de entrenamiento.

### `security`

Contiene la infraestructura transversal de autenticación:

- Filtro JWT.
- Servicio para crear y validar tokens.
- Configuración relacionada con `UserDetails`.

### `config`

Contiene la configuración global de Spring Boot, incluida la cadena de filtros de seguridad.

### `shared`

Solo debe contener elementos utilizados por varios módulos, por ejemplo:

- Excepciones comunes.
- Manejadores globales de errores.
- Respuestas HTTP compartidas.

No debe convertirse en un paquete donde se acumulen clases sin dueño claro.

## Reglas de dependencia

1. Un módulo debe depender de interfaces o servicios de otro módulo cuando sea necesario, evitando acceder directamente a sus detalles internos.
2. Los DTOs deben pertenecer al módulo que expone el caso de uso.
3. Las entidades no deben utilizarse directamente como respuesta de los controladores.
4. Los controladores solo deben coordinar peticiones HTTP y delegar la lógica al servicio.
5. Los repositorios deben ser utilizados por los servicios, no directamente por los controladores.
6. `shared` y `security` son transversales; los módulos de negocio no deben depender entre sí sin una razón concreta.
7. Las relaciones entre entidades JPA deben mantenerse controladas para evitar respuestas JSON recursivas.

## Orden de migración

La migración debe hacerse de forma incremental para mantener el proyecto compilable:

1. Crear los nuevos paquetes sin modificar la lógica de negocio.
2. Mover `login` a `auth/dto`.
3. Mover `AuthController` y `AuthService` a `auth`.
4. Mover `JwtService` y `jwtAuthenticationFilter` a `security`.
5. Mover `Usuario` y `UsuarioRepository` a `usuario`.
6. Mover `Ejercicio` y sus clases relacionadas a `ejercicio`.
7. Mover `Rutina`, `RutinaEjercicio` y `DiaSemana` a `rutina`.
8. Mover `Entrenamiento` y `RegistroSerie` a `entrenamiento`.
9. Actualizar declaraciones `package` e imports.
10. Eliminar los paquetes antiguos cuando no tengan referencias.
11. Ejecutar las pruebas después de cada módulo:

```powershell
mvn test
```

La aplicación principal está ubicada en `com.fittrack`, por lo que Spring continuará detectando los componentes de los nuevos subpaquetes automáticamente.

## Problemas actuales para corregir durante la implementación

- En `AuthController`, `authService` debe ser `final` para que `@RequiredArgsConstructor` genere correctamente la inyección por constructor.
- El endpoint de registro debe estar permitido públicamente junto con el endpoint de login.
- El filtro JWT actualmente extrae el token, pero todavía no lo valida ni autentica al usuario.
- La clave JWT no debe estar escrita directamente en el código; debe obtenerse desde propiedades o variables de entorno.
- Las entidades no deberían exponerse directamente desde los endpoints; deben utilizarse DTOs.
- Los servicios y controladores vacíos deben implementarse solo cuando exista un caso de uso definido.

## Criterio de finalización

La migración estará terminada cuando:

- No existan los paquetes globales antiguos `controller`, `service`, `entity` y `repository`.
- Cada funcionalidad tenga sus propias clases agrupadas dentro de su módulo.
- El proyecto compile y las pruebas pasen con `mvn test`.
- Los controladores no accedan directamente a repositorios.
- Las respuestas de la API utilicen DTOs.
- Las dependencias entre módulos estén justificadas y sean fáciles de identificar.
