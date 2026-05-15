package com.utn.backend.model;

import java.time.LocalDateTime;

public abstract class Base {
    public Long id;
    public boolean eliminado;
    public LocalDateTime createdAt;
}
