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
| `nombre` | `varchar` |  |

