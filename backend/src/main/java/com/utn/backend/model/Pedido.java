package com.utn.backend.model;

import java.time.LocalDate;
import java.util.HashSet;

import com.utn.backend.enums.*;

public class Pedido extends Base implements Calculable{
    private LocalDate fecha;
    private Estado estado;
    private Double total;
    private FormaPago formaPago;
    private HashSet<DetallePedido> detallePedidos = new HashSet<>();

    public void addDetallePedido(int cantidad, Producto producto){};
    public DetallePedido findDetallePedidoByProducto(Producto producto){return new DetallePedido();};
    public void deleteDetallePedidoByProducto(Producto producto){};

    @Override
    public void calacularTotal() {

    }
}
