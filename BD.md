## Table `ejercicio`

### Columns

| Name | Type | Constraints |
|------|------|-------------|
| `id` | `int8` | Primary |
| `grupo_muscular` | `varchar` |  |
| `nombre` | `varchar` |  |
| `id_usuario` | `int8` |  |

## Table `entrenamiento`

### Columns

| Name | Type | Constraints |
|------|------|-------------|
| `id` | `int8` | Primary |
| `duracion_minutos` | `int4` |  |
| `series_totales` | `int4` |  Nullable |
| `fecha` | `date` |  |
| `notas` | `varchar` |  Nullable |
| `url_foto` | `varchar` |  Nullable |
| `status` | `bool` |  Not Null Default: `true` |
| `id_rutina` | `int8` |  |
| `id_usuario` | `int8` |  |

`entrenamiento.status` implementa el borrado lógico. La columna `url_foto`
guarda la ruta interna de la imagen dentro del bucket privado de Supabase
Storage, no una URL firmada temporal.

`entrenamiento.series_totales` conserva cuántas series existían al terminar
la sesión. Los entrenamientos antiguos pueden mantenerla en `null`; en ese
caso el backend usa como respaldo la cantidad de series registradas.

## Table `registro_serie`

### Columns

| Name | Type | Constraints |
|------|------|-------------|
| `id` | `int8` | Primary |
| `numero_serie` | `int4` |  |
| `peso` | `numeric` |  |
| `repeticiones` | `int4` |  |
| `id_ejercicio` | `int8` |  |
| `id_rutina_ejercicio_origen` | `int8` |  |
| `orden_ejercicio` | `int4` |  |
| `id_entrenamiento` | `int8` |  |

Cada registro conserva el ejercicio, el ID original del bloque de
`rutina_ejercicio` y su orden al finalizar el entrenamiento. El ID de origen no
es una clave foránea: funciona como una referencia histórica para que una
edición posterior de la rutina no rompa entrenamientos guardados. La combinación
`id_entrenamiento`, `id_rutina_ejercicio_origen` y `numero_serie` es única.

## Table `rutina`

### Columns

| Name | Type | Constraints |
|------|------|-------------|
| `id` | `int8` | Primary |
| `descripcion` | `varchar` |  Nullable |
| `dia_semana` | `varchar` |  |
| `nombre` | `varchar` |  |
| `status` | `bool` |  Not Null Default: `true` |
| `id_usuario` | `int8` |  |

> `rutina.status` habilita el borrado logico: `DELETE /rutinas/{id}` lo pasa a
> `false` sin eliminar la fila ni sus registros de `rutina_ejercicio`. Si tu base
> de datos fue creada antes de esta columna, agregala con:
>
> ```sql
> ALTER TABLE rutina
> ADD COLUMN IF NOT EXISTS status BOOLEAN NOT NULL DEFAULT TRUE;
> ```
>
> Con `spring.jpa.hibernate.ddl-auto=update` Hibernate tambien la crea al arrancar.

## Table `rutina_ejercicio`

### Columns

| Name | Type | Constraints |
|------|------|-------------|
| `id` | `int8` | Primary |
| `orden` | `int4` |  Nullable |
| `id_ejercicio` | `int8` |  |
| `id_rutina` | `int8` |  |

`rutina_ejercicio` representa la aparición de un ejercicio dentro de una rutina.
La configuración de cada serie planificada se almacena por separado en
`rutina_serie`.

## Table `rutina_serie`

### Columns

| Name | Type | Constraints |
|------|------|-------------|
| `id` | `int8` | Primary |
| `numero_serie` | `int4` |  |
| `repeticiones_objetivo` | `int4` |  |
| `peso_objetivo` | `numeric` |  |
| `id_rutina_ejercicio` | `int8` |  |

`rutina_serie` contiene una fila por cada serie planificada. Es diferente de
`registro_serie`: la primera representa el objetivo de la rutina y la segunda
registra lo que el usuario realizó durante un entrenamiento.

## Table `usuario`

### Columns

| Name | Type | Constraints |
|------|------|-------------|
| `id` | `int8` | Primary |
| `contrasena_hash` | `varchar` |  |
| `correo` | `varchar` |  Unique |
| `fecha_registro` | `date` |  |
| `foto_perfil_ruta` | `varchar(500)` | Nullable |
| `nombre` | `varchar` |  |

`foto_perfil_ruta` guarda la ruta interna del objeto en el bucket privado, no
la URL firmada temporal. Para bases existentes se puede agregar explícitamente:

```sql
ALTER TABLE usuario
ADD COLUMN IF NOT EXISTS foto_perfil_ruta VARCHAR(500);
```

Con `spring.jpa.hibernate.ddl-auto=update`, Hibernate también crea la columna
al iniciar la aplicación.

