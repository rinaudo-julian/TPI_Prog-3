package com.utn.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity(name = "categorias")
public class Categoria extends Base {
    @Column(nullable = false, unique = true)
    private String nombre;
    private String descripcion;
    @OneToMany(mappedBy = "categoria")
    private Set<Producto> productos = new HashSet<>();
}
