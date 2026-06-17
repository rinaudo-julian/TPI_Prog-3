package com.utn.backend.repository;

import com.utn.backend.model.Pedido;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends BaseRepository<Pedido> {
    @Query("""
            select distinct p
            from Pedido p
            left join fetch p.detallePedidos dp
            left join fetch dp.producto pr
            left join fetch pr.categoria
            where p.usuario.id = :usuarioId
              and p.eliminado = false
            """)
    List<Pedido> findAllByUsuarioIdAndEliminadoFalse(@Param("usuarioId") Long usuarioId);
}
