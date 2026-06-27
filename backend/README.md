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

## Notas

- La base H2 es en memoria.
- El seed de datos se carga desde `src/main/resources/data.sql`.
- El usuario admin por defecto.
