package com.utn.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "categorias")
public class Categoria extends Base {
    @Column(nullable = false, unique = true)
    private String nombre;
    private String descripcion;
    @Transient
    private Set<Producto> productos = new HashSet<>();
}
