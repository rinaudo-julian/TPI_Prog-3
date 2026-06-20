package com.utn.backend.model;

import com.utn.backend.enums.Estado;
import com.utn.backend.enums.FormaPago;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "pedidos")
public class Pedido extends Base implements Calculable {
    @Column(nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado;

    @Column(nullable = false)
    private Double total;

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false)
    private String direccion;

    @Column
    private String notaAdicional;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormaPago formaPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DetallePedido> detallePedidos = new HashSet<>();

    public void addDetallePedido(int cantidad, Producto producto) {
        DetallePedido detallePedido = new DetallePedido();
        detallePedido.setCantidad(cantidad);
        detallePedido.setSubtotal(producto.getPrecio() * cantidad);
        detallePedido.setProducto(producto);
        detallePedido.setPedido(this);
        detallePedidos.add(detallePedido);
    }

    public DetallePedido findDetallePedidoByProducto(Producto producto) {
        return detallePedidos.stream()
                .filter(detalle -> detalle.getProducto() != null
                        && producto != null
                        && Objects.equals(detalle.getProducto().getId(), producto.getId()))
                .findFirst()
                .orElse(null);
    }

    public void deleteDetallePedidoByProducto(Producto producto) {
        DetallePedido detallePedido = findDetallePedidoByProducto(producto);
        if (detallePedido != null) {
            detallePedidos.remove(detallePedido);
            detallePedido.setPedido(null);
        }
    }

    @Override
    public void calcularTotal() {
        total = detallePedidos.stream()
                .mapToDouble(DetallePedido::getSubtotal)
                .sum();
    }
}
