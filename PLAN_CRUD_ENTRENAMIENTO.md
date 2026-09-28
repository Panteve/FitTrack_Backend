# Plan de implementación: CRUD de entrenamientos y foto en Supabase Storage

## Objetivo

Implementar el CRUD completo de `Entrenamiento` en el backend Spring Boot. Un
entrenamiento pertenece al usuario autenticado, parte de una rutina activa,
contiene las series realmente ejecutadas y puede tener una fotografía opcional
almacenada en un bucket privado de Supabase Storage.

La eliminación será lógica mediante `status = false`. No se deben eliminar
físicamente el entrenamiento, sus registros de series ni su fotografía cuando
se invoque `DELETE`.

## Restricciones obligatorias

- No crear pruebas unitarias, de integración, de controlador ni de repositorio.
- No ejecutar pruebas existentes.
- No iniciar la aplicación.
- No conectarse a Supabase, no crear el bucket y no ejecutar SQL desde esta
  tarea.
- No incluir credenciales, `service_role`, URLs privadas ni secretos en el
  código fuente.
- No exponer entidades JPA directamente desde los controladores.
- Mantener el estilo actual del proyecto: DTOs como `record`, inyección por
  constructor, `ApiException`, `GlobalExceptionHandler` y
  `UsuarioAutenticado`.
- Se permite únicamente compilar con pruebas omitidas al finalizar, por
  ejemplo `./mvnw -DskipTests compile`. La compilación no debe arrancar Spring.

## Decisión de modelo para ejercicios repetidos

Una rutina puede contener el mismo ejercicio varias veces. Por eso un
`RegistroSerie` no debe identificar solamente al ejercicio general: también
debe conservar cuál era el bloque concreto `RutinaEjercicio` al finalizar.

Conservar la relación con el ejercicio y agregar una referencia histórica al
bloque:

```text
registro_serie -> ejercicio
registro_serie.id_rutina_ejercicio_origen
registro_serie.orden_ejercicio
```

Así dos apariciones del mismo ejercicio se distinguen mediante IDs de origen
diferentes, pero actualizar una rutina no rompe el historial al eliminar sus
bloques anteriores.

## Modelo de datos y entidades

### `Entrenamiento`

Conservar:

- `id`
- `usuario`
- `rutina`
- `fecha`
- `duracionMinutos`
- `notas`
- `urlFoto`

Modificar y agregar:

- Hacer `urlFoto` nullable. La foto es opcional y el entrenamiento debe poder
  guardarse aunque la subida falle.
- Agregar `status` como `Boolean`, no nulo, inicializado en `true` y con una
  definición compatible con PostgreSQL: `boolean not null default true`.
- Agregar `List<RegistroSerie> registrosSeries` con:
  - `mappedBy = "entrenamiento"`
  - `cascade = CascadeType.ALL`
  - `orphanRemoval = true`
  - orden estable por bloque y número de serie.
- Agregar métodos `agregarRegistroSerie(...)` y
  `reemplazarRegistrosSeries(...)` que mantengan ambos lados de la relación.
- El constructor de creación debe dejar el entrenamiento activo y sin obligar
  a recibir una fotografía.

### `RegistroSerie`

- Mantener `Ejercicio ejercicio` mediante `id_ejercicio`.
- Guardar `rutinaEjercicioId` en `id_rutina_ejercicio_origen` como valor
  histórico, sin clave foránea hacia el bloque mutable.
- Guardar `ordenEjercicio` como copia del orden al finalizar.
- Mantener:
  - `id`
  - `entrenamiento`
  - `numeroSerie`
  - `repeticiones`
  - `peso`
- Configurar las relaciones `ManyToOne` como `LAZY`.
- Agregar una restricción única para
  `(id_entrenamiento, id_rutina_ejercicio_origen, numero_serie)`.
- El nombre y el ID del ejercicio se obtienen mediante
  `registroSerie.getEjercicio()`.

### Documentación de base de datos

Actualizar `BD.md` para documentar:

- `entrenamiento.status`.
- `entrenamiento.url_foto` como nullable y usado para guardar la ruta interna
  de Storage, no una URL firmada temporal.
- La colección lógica de registros del entrenamiento.
- La referencia histórica `registro_serie.id_rutina_ejercicio_origen` y la
  copia `registro_serie.orden_ejercicio`.
- La restricción única de las series ejecutadas.

No crear migraciones ni modificar directamente la base de datos.

## DTOs

Crear los DTOs dentro de `com.fittrack.entrenamiento.dto`.

### `RegistroSerieRequest`

```json
{
  "rutinaEjercicioId": 31,
  "numeroSerie": 1,
  "repeticiones": 12,
  "peso": 60.00
}
```

Validaciones:

- `rutinaEjercicioId`: `@NotNull` y positivo.
- `numeroSerie`: `@NotNull` y positivo.
- `repeticiones`: `@NotNull` y positivo.
- `peso`: `@NotNull`, mayor o igual a cero y con máximo dos decimales.

### `EntrenamientoCrearDto`

```json
{
  "rutinaId": 5,
  "fecha": "2026-09-27",
  "duracionMinutos": 55,
  "notas": "Buen entrenamiento",
  "series": [
    {
      "rutinaEjercicioId": 31,
      "numeroSerie": 1,
      "repeticiones": 12,
      "peso": 60.00
    }
  ]
}
```

Validaciones:

- `rutinaId`: requerido y positivo.
- `fecha`: requerida y `@PastOrPresent`.
- `duracionMinutos`: requerida y positiva.
- `notas`: opcional, con un límite razonable de caracteres.
- `series`: requerida, no vacía y con `@Valid` en sus elementos.
- No recibir `usuarioId`, `status`, `urlFoto` ni una URL suministrada por el
  frontend.

### `EntrenamientoActualizarDto`

El `PUT` será reemplazo completo de los campos editables y tendrá la misma
estructura y validaciones que la creación. No debe modificar `urlFoto`; la foto
se administra exclusivamente mediante su endpoint.

### Respuestas

Crear:

- `RegistroSerieDetalleDto`: `id`, `rutinaEjercicioId`, `ejercicioId`,
  `nombreEjercicio`, `ordenEjercicio`, `numeroSerie`, `repeticiones` y `peso`.
- `EntrenamientoDto` para listado resumido: `id`, `rutinaId`, `nombreRutina`,
  `fecha`, `duracionMinutos`, `notas` y `tieneFoto`. No generar una URL firmada
  por cada fila del listado.
- `EntrenamientoDetalleDto`: datos del entrenamiento, lista ordenada de series
  y `fotoUrl` opcional. Si existe una ruta, `fotoUrl` será una URL firmada
  temporal generada por el backend.
- `EntrenamientoFotoDto`: `entrenamientoId` y `fotoUrl`.

Ordenar las series primero por `RutinaEjercicio.orden` y después por
`numeroSerie`.

## Endpoints

Implementar en `EntrenamientoController`:

| Método | Ruta | Resultado |
|---|---|---|
| `GET` | `/entrenamientos` | Lista resumida de entrenamientos activos del usuario autenticado |
| `GET` | `/entrenamientos/{id}` | Detalle de un entrenamiento activo con sus series y foto |
| `POST` | `/entrenamientos` | Crea/finaliza un entrenamiento con sus series realizadas |
| `PUT` | `/entrenamientos/{id}` | Reemplaza los datos editables y todas sus series |
| `DELETE` | `/entrenamientos/{id}` | Soft delete, responde `204 No Content` |
| `POST` | `/entrenamientos/{id}/foto` | Sube o reemplaza la foto opcional mediante multipart |

Todos los endpoints deben obtener el usuario desde:

```java
@AuthenticationPrincipal UsuarioAutenticado usuario
```

Respuestas HTTP:

- `POST /entrenamientos`: `201 Created` con body, sin header `Location`.
- `GET` y `PUT`: `200 OK`.
- `DELETE`: `204 No Content`.
- Foto: `200 OK` con `EntrenamientoFotoDto`.
- Recurso inexistente, inactivo o de otro usuario: `404 Not Found`, evitando
  revelar que pertenece a otra persona.
- Datos o asociaciones inválidas: `400 Bad Request`.
- Tipo de archivo no admitido: `415 Unsupported Media Type`.
- Fallo de Supabase Storage: `502 Bad Gateway` con mensaje seguro, sin devolver
  la respuesta interna ni credenciales de Supabase.

El endpoint de foto debe declararse como:

```java
@PostMapping(
        value = "/{id}/foto",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
```

y recibir un `@RequestPart("foto") MultipartFile foto`.

## Reglas de negocio del servicio

### Crear

1. Buscar la rutina mediante `rutinaId`, `usuarioId` y `status = true`.
2. Cargar sus `rutinaEjercicios` y ejercicios asociados.
3. Validar que cada `rutinaEjercicioId` enviado pertenece exactamente a esa
   rutina. No validar la propiedad del ejercicio individual: una rutina puede
   usar ejercicios globales o de otros usuarios según las reglas existentes.
4. Validar que no se repita la combinación
   `(rutinaEjercicioId, numeroSerie)` en el mismo request.
5. Normalizar `notas`: convertir `null`, vacío o solo espacios en `null`; en
   otro caso aplicar `trim()`.
6. Construir el agregado `Entrenamiento -> RegistroSerie`, manteniendo ambos
   lados de las relaciones.
7. Persistir con cascada desde `Entrenamiento` dentro de `@Transactional`.
8. Usar `saveAndFlush` solamente si es necesario para devolver los IDs
   generados en la respuesta.

### Listar y obtener por ID

- Filtrar siempre por `usuario.id` y `status = true` desde el repositorio.
- El listado debe utilizar una consulta resumida/proyección o una carga que no
  provoque N+1; no necesita traer `RegistroSerie` ni generar URLs firmadas.
- El detalle debe cargar en una sola consulta JPA el entrenamiento, su rutina,
  sus registros y sus ejercicios.
- Como el detalle carga una sola colección (`registrosSeries`), puede usarse
  `@EntityGraph` o un `join fetch` con `distinct`.
- Generar una URL firmada solo si `urlFoto` contiene una ruta.

### Actualizar

- Buscar el entrenamiento activo por `id` y usuario autenticado.
- Validar la rutina y todos los bloques igual que en creación.
- Actualizar rutina, fecha, duración y notas.
- Reemplazar las series con `clear()` y métodos de agregado, confiando en
  `orphanRemoval`; no usar borrado bulk porque evita las cascadas de JPA.
- Mantener la fotografía actual sin cambios.
- Ejecutar toda la operación dentro de una sola transacción.

### Soft delete

- Buscar por `id` y `usuarioId` sin filtrar por estado para que la operación sea
  idempotente.
- Si ya está inactivo, terminar sin error.
- Si está activo, asignar `status = false` mediante dirty checking.
- No eliminar series, la fotografía de Storage ni asociaciones.
- Los `GET`, `PUT` y subida de foto deben ignorar entrenamientos inactivos.

## Integración con Supabase Storage

### Configuración

Crear propiedades tipadas, por ejemplo `SupabaseStorageProperties`, usando
`@ConfigurationProperties`. Leer exclusivamente variables de entorno:

```properties
supabase.storage.url=${SUPABASE_URL}
supabase.storage.service-key=${SUPABASE_SECRET_KEY:${SUPABASE_SERVICE_ROLE_KEY:}}
supabase.storage.bucket=${SUPABASE_STORAGE_BUCKET:fotos-entrenamientos}
supabase.storage.signed-url-seconds=${SUPABASE_SIGNED_URL_SECONDS:3600}
```

- Registrar las propiedades con `@ConfigurationPropertiesScan` o
  `@EnableConfigurationProperties`.
- Nunca colocar valores reales en `application*.properties`, `.env.example`,
  logs, DTOs ni Swagger.
- La llave de servicio vive únicamente en el backend; Android nunca debe
  recibirla.
- Documentar en el plan de entrega que el bucket privado
  `fotos-entrenamientos` debe crearse manualmente en Supabase.

### Servicio de almacenamiento

- Crear una abstracción `FotoStorageService` y una implementación para
  Supabase usando el cliente HTTP disponible en Spring (`RestClient`).
- Responsabilidades:
  - subir bytes con el content type correcto;
  - generar URL firmada temporal;
  - eliminar la imagen anterior cuando haya sido reemplazada correctamente;
  - traducir fallos HTTP de Supabase a una excepción controlada.
- Guardar en `Entrenamiento.urlFoto` solamente una ruta como:

```text
{usuarioId}/{entrenamientoId}/{uuid}.jpg
```

- Usar siempre UUID y `upsert = false` para evitar colisiones y problemas de
  caché.
- No guardar la URL firmada porque expira.

### Validación de imágenes

- Foto obligatoria únicamente en el endpoint de foto; el entrenamiento puede
  existir sin ella.
- Rechazar archivos vacíos.
- Tamaño máximo: 5 MB.
- Aceptar inicialmente `image/jpeg` y `image/png`.
- Generar la extensión desde el tipo admitido; no confiar en el nombre original
  entregado por Android.
- Configurar también los límites multipart de Spring a 5 MB.

### Consistencia al reemplazar una foto

1. Validar primero el entrenamiento y el archivo.
2. Subir la nueva imagen a una ruta UUID diferente.
3. Guardar la nueva ruta en el entrenamiento.
4. Solo después de guardar correctamente, intentar eliminar la imagen anterior.
5. Si falla la subida nueva, conservar la ruta anterior.
6. Si falla únicamente la eliminación anterior, registrar un warning sin
   deshacer el cambio exitoso ni exponer secretos.

No asumir una transacción distribuida entre PostgreSQL y Supabase Storage.

## Repositorios

Ampliar `EntrenamientoRepository` con consultas explícitas para:

- Listar entrenamientos activos del usuario ordenados por fecha descendente e
  ID descendente.
- Buscar el detalle activo por `id` y `usuarioId`, cargando las relaciones
  necesarias sin N+1.
- Buscar por `id` y `usuarioId` sin filtrar estado para el soft delete.

Ampliar `RutinaRepository` solo si hace falta una consulta que cargue en una
sola operación la rutina activa del usuario, sus bloques y sus ejercicios.
Evitar consultar individualmente cada `rutinaEjercicioId`.

No es necesario conservar `RegistroSerieRepository` si todas las series se
administran como parte del agregado `Entrenamiento`; eliminarlo solamente si se
confirma que no tiene otro uso. El controlador y servicio vacíos de
`RegistroSerie` tampoco deben exponer un CRUD independiente: las series se
crean y reemplazan únicamente a través del entrenamiento.

## Manejo global de errores

- Mantener el formato actual de `ApiErrorResponseDto`.
- Adaptar el mensaje de `HttpMediaTypeNotSupportedException`: actualmente
  indica siempre que se use JSON, pero el nuevo endpoint acepta multipart.
  Producir un mensaje genérico basado en los tipos soportados.
- Agregar manejo de límite multipart excedido, devolviendo `413 Payload Too
  Large`.
- Los errores del proveedor de almacenamiento deben quedar registrados con
  contexto técnico seguro; nunca registrar headers de autorización o la llave
  de servicio.

## Swagger/OpenAPI

- Documentar `EntrenamientoController` con `@Tag`.
- Agregar `@Operation` y respuestas principales con `@ApiResponse`.
- Incluir ejemplos de los JSON de creación y actualización.
- Documentar el endpoint de fotografía como `multipart/form-data` y el campo
  binario `foto`.
- Mantener el esquema Bearer configurado globalmente en `OpenApiConfig`.
- Actualizar la descripción general de OpenAPI para incluir entrenamientos.

## Android: contrato que debe quedar estable

El backend debe permitir este flujo sin que Android conozca las credenciales de
Supabase:

1. Android llama `POST /entrenamientos` con JSON.
2. Recibe el ID del entrenamiento.
3. Si el usuario seleccionó una foto, Android llama
   `POST /entrenamientos/{id}/foto` con `multipart/form-data`.
4. Si la foto falla, el entrenamiento permanece guardado y Android puede
   reintentar únicamente la segunda petición.
5. Android usa la `fotoUrl` temporal entregada por `GET /entrenamientos/{id}`
   para mostrarla con Glide.

Aunque Android ejecute dos solicitudes, para el usuario será una sola acción en
el botón de finalizar.

## Orden recomendado de implementación

1. Ajustar `Entrenamiento` y `RegistroSerie` y documentar el modelo en `BD.md`.
2. Crear DTOs con Bean Validation.
3. Implementar consultas de repositorio optimizadas.
4. Implementar mapeo y reglas de negocio en `EntrenamientoService`.
5. Implementar los cinco endpoints JSON/soft delete del CRUD.
6. Crear configuración y abstracción de Supabase Storage.
7. Implementar el endpoint multipart de fotografía.
8. Adaptar manejo global de errores multipart y Storage.
9. Completar Swagger/OpenAPI.
10. Ejecutar revisión estática y, si el entorno lo permite, compilar con las
    pruebas omitidas. No iniciar la aplicación.

## Validación final sin pruebas

- Confirmar mediante búsqueda estática que ningún endpoint acepta `usuarioId`
  desde el body.
- Confirmar que todas las consultas filtran por el usuario autenticado.
- Confirmar que los endpoints normales excluyen `status = false`.
- Confirmar que `DELETE` solo cambia el estado.
- Confirmar que `PUT` conserva la foto.
- Confirmar que el mismo ejercicio puede distinguirse si aparece varias veces
  gracias a `rutinaEjercicioId`.
- Confirmar que una serie no puede usar un bloque perteneciente a otra rutina.
- Confirmar que no existen accesos N+1 en listado y detalle.
- Confirmar que la respuesta de listado no genera una URL firmada por fila.
- Confirmar que no hay claves de Supabase hardcodeadas ni registradas en logs.
- Confirmar que `urlFoto` guarda una ruta y no una URL temporal.
- Confirmar que no se crearon archivos bajo `src/test`.
- Confirmar que no se ejecutaron pruebas ni se inició Spring Boot.

## Fuera de alcance

- Cambios en la aplicación Android.
- Creación automática del bucket de Supabase.
- Políticas RLS para subida directa desde Android.
- Migraciones SQL o ejecución de cambios sobre Supabase.
- Borrado físico o restauración de entrenamientos.
- Endpoint separado para CRUD de `RegistroSerie`.
- Pruebas automáticas de cualquier tipo.
