package com.utn.backend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public boolean eliminado;
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    public LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(nullable = false)
    public LocalDateTime updatedAt;
    @Version
    public Long version;
}
