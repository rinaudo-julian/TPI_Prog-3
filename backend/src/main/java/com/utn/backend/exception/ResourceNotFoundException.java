package com.utn.backend.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {
        super("Recurso no encontrado");
    }
}
