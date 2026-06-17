package com.utn.backend.service.impl;

import com.utn.backend.dto.DetallePedidoCreateRequestDTO;
import com.utn.backend.dto.PedidoCreateRequestDTO;
import com.utn.backend.dto.PedidoResponseDTO;
import com.utn.backend.exception.BusinessException;
import com.utn.backend.exception.ResourceNotFoundException;
import com.utn.backend.model.Pedido;
import com.utn.backend.model.Producto;
import com.utn.backend.repository.PedidoRepository;
import com.utn.backend.repository.ProductoRepository;
import com.utn.backend.repository.UsuarioRepository;
import com.utn.backend.mappers.PedidoMapper;
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
    private final PedidoMapper pedidoMapper;

    @Transactional
    public PedidoResponseDTO save(PedidoCreateRequestDTO requestDTO) {
        var usuario = usuarioRepository.findByIdAndEliminadoFalse(requestDTO.idUsuario())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Entidad con id " + requestDTO.idUsuario() + " no encontrado"));

        Map<Long, Integer> cantidadPorProducto = requestDTO.detallePedido().stream()
                .collect(Collectors.toMap(
                        DetallePedidoCreateRequestDTO::idProducto,
                        DetallePedidoCreateRequestDTO::cantidad,
                        Integer::sum,
                        LinkedHashMap::new));

        List<Producto> productos = productoRepository.findAllById(cantidadPorProducto.keySet());

        Pedido pedido = new Pedido();
        pedido.setFecha(LocalDate.now());
        pedido.setEstado(requestDTO.estado());
        pedido.setFormaPago(requestDTO.formaPago());
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

        return pedidoMapper.toDto(pedido);
    }

    @Transactional
    public List<PedidoResponseDTO> findAll() {
        return pedidoRepository.findAll().stream()
                .map(pedidoMapper::toDto)
                .toList();
    }

    @Transactional
    public PedidoResponseDTO findById(Long id) {
        Pedido pedido = pedidoRepository.findByIdOrThrow(id);
        return pedidoMapper.toDto(pedido);
    }
}
