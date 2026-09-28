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
| `fecha` | `date` |  |
| `notas` | `varchar` |  Nullable |
| `url_foto` | `varchar` |  |
| `id_rutina` | `int8` |  |
| `id_usuario` | `int8` |  |

## Table `registro_serie`

### Columns

| Name | Type | Constraints |
|------|------|-------------|
| `id` | `int8` | Primary |
| `numero_serie` | `int4` |  |
| `peso` | `numeric` |  |
| `repeticiones` | `int4` |  |
| `id_ejercicio` | `int8` |  |
| `id_entrenamiento` | `int8` |  |

## Table `rutina`

### Columns

| Name | Type | Constraints |
|------|------|-------------|
| `id` | `int8` | Primary |
| `descripcion` | `varchar` |  Nullable |
| `dia_semana` | `varchar` |  |
| `nombre` | `varchar` |  |
| `id_usuario` | `int8` |  |

## Table `rutina_ejercicio`

### Columns

| Name | Type | Constraints |
|------|------|-------------|
| `id` | `int8` | Primary |
| `orden` | `int4` |  Nullable |
| `peso_objetivo` | `numeric` |  |
| `repeticiones_objetivo` | `int4` |  |
| `series_objetivo` | `int4` |  |
| `id_ejercicio` | `int8` |  |
| `id_rutina` | `int8` |  |

## Table `usuario`

### Columns

| Name | Type | Constraints |
|------|------|-------------|
| `id` | `int8` | Primary |
| `contrasena_hash` | `varchar` |  |
| `correo` | `varchar` |  Unique |
| `fecha_registro` | `date` |  |
| `nombre` | `varchar` |  |

