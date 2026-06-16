package com.utn.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "productos")
public class Producto extends Base {
    @Column(nullable = false, unique = true)
    private String nombre;
    private String descripcion;
    @Column(nullable = false)
    private Double precio;
    @Column(nullable = false)
    private int stock;
    private String imagen;
    private boolean disponible;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
}
