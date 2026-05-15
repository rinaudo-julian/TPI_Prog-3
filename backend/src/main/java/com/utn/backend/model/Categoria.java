package com.utn.backend.model;

import java.util.HashSet;

public class Categoria extends Base {
    private String nombre;
    private String descripcion;
    private HashSet<Producto> productos = new HashSet<>();
}
