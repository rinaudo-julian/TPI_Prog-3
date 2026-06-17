package com.utn.backend.service.impl;

import com.utn.backend.dto.DetallePedidoCreateRequestDTO;
import com.utn.backend.dto.DetallePedidoResponseDTO;
import com.utn.backend.dto.PedidoCreateRequestDTO;
import com.utn.backend.dto.PedidoResponseDTO;
import com.utn.backend.dto.ProductoResponseDTO;
import com.utn.backend.dto.CategoriaResponseDTO;
import com.utn.backend.exception.BusinessException;
import com.utn.backend.exception.ResourceNotFoundException;
import com.utn.backend.model.DetallePedido;
import com.utn.backend.model.Pedido;
import com.utn.backend.model.Producto;
import com.utn.backend.repository.PedidoRepository;
import com.utn.backend.repository.ProductoRepository;
import com.utn.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public PedidoResponseDTO save(PedidoCreateRequestDTO requestDTO) {
        var usuario = usuarioRepository.findByIdAndEliminadoFalse(requestDTO.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Entidad con id " + requestDTO.getIdUsuario() + " no encontrado"));

        List<DetallePedidoCreateRequestDTO> detalles = requestDTO.getDetallePedido();

        Map<Long, Integer> cantidadPorProducto = detalles.stream()
                .collect(Collectors.toMap(
                        DetallePedidoCreateRequestDTO::getIdProducto,
                        DetallePedidoCreateRequestDTO::getCantidad,
                        Integer::sum,
                        LinkedHashMap::new));

        List<Producto> productos = productoRepository.findAllById(cantidadPorProducto.keySet());

        Pedido pedido = new Pedido();
        pedido.setFecha(LocalDate.now());
        pedido.setEstado(requestDTO.getEstado());
        pedido.setFormaPago(requestDTO.getFormaPago());
        pedido.setUsuario(usuario);

        for (Producto producto : productos) {
            if (producto.isEliminado()) {
                throw new ResourceNotFoundException("El producto '" + producto.getNombre() + "' no existe");
            }

            if (!producto.isDisponible()) {
                throw new BusinessException(
                        "El producto '" + producto.getNombre() + "' no está disponible para la venta");
            }

            Integer cantidadSolicitada = cantidadPorProducto.get(producto.getId());
            if (!producto.tieneStockSuficiente(cantidadSolicitada)) {
                throw new BusinessException("Stock insuficiente para '" + producto.getNombre() + "'. Disponible: "
                        + producto.getStock() + ", Solicitado: " + cantidadSolicitada);
            }

            producto.reducirStock(cantidadSolicitada);
            pedido.addDetallePedido(cantidadSolicitada, producto);
        }

        pedido.calcularTotal();
        pedido = pedidoRepository.save(pedido);

        return toDto(pedido);
    }

    private PedidoResponseDTO toDto(Pedido pedido) {
        PedidoResponseDTO response = new PedidoResponseDTO();
        response.setId(pedido.getId());
        response.setFecha(pedido.getFecha());
        response.setEstado(pedido.getEstado());
        response.setTotal(pedido.getTotal());
        response.setFormaPago(pedido.getFormaPago());
        response.setIdUsuario(pedido.getUsuario().getId());
        response.setDetalles(pedido.getDetallePedidos().stream()
                .map(this::toDto)
                .toList());
        return response;
    }

    private DetallePedidoResponseDTO toDto(DetallePedido detallePedido) {
        DetallePedidoResponseDTO response = new DetallePedidoResponseDTO();
        response.setId(detallePedido.getId());
        response.setCantidad(detallePedido.getCantidad());
        response.setSubtotal(detallePedido.getSubtotal());
        response.setProducto(toProductoDto(detallePedido.getProducto()));
        return response;
    }

    private ProductoResponseDTO toProductoDto(Producto producto) {
        ProductoResponseDTO response = new ProductoResponseDTO();
        response.setId(producto.getId());
        response.setNombre(producto.getNombre());
        response.setPrecio(producto.getPrecio());
        response.setDescripcion(producto.getDescripcion());
        response.setStock(producto.getStock());
        response.setImagen(producto.getImagen());
        response.setDisponible(producto.isDisponible());

        if (producto.getCategoria() != null) {
            CategoriaResponseDTO categoria = new CategoriaResponseDTO();
            categoria.setId(producto.getCategoria().getId());
            categoria.setNombre(producto.getCategoria().getNombre());
            categoria.setDescripcion(producto.getCategoria().getDescripcion());
            response.setCategoria(categoria);
        }

        return response;
    }
}
