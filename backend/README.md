# Backend

API Spring Boot con persistencia en H2 en memoria.

## Requisitos

- Java 17

## Instalación y ejecución

1. Entrar a la carpeta `backend`.
2. Levantar la aplicación:

```bash
./gradlew bootRun
```

La API queda disponible en `http://localhost:8080`.

Luego de levantar el proyecto, podés abrir:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html#`
- H2 Console: `http://localhost:8080/h2-console/login.jsp`

Datos para ingresar a H2:

- JDBC URL: `jdbc:h2:mem:mydb`
- User Name: `sa`
- Password: vacío

## Notas

- La base H2 es en memoria.
- El seed de datos se carga desde `src/main/resources/data.sql`.
- El usuario admin por defecto.
