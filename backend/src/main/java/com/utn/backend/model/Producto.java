package com.utn.backend.model;

public class Producto extends Base{
    private String nombre;
    private String descripcion;
    private Double precio;
    private int stock;
    private String imagen;
    private boolean disponible;
    private Categoria categoria;
}
