package com.utn.backend.model;

import com.utn.backend.enums.Rol;

import java.util.HashSet;

public class Usuario extends Base{
    private String nombre;
    private String apellido;
    private String email;
    private String celular;
    private String contrasena;
    private Rol rol;
    private HashSet<Pedido> pedido  = new HashSet<>();
}
