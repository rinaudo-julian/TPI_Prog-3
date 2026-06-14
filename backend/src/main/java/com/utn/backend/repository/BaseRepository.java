package com.utn.backend.repository;

import com.utn.backend.exception.ResourceNotFoundException;
import com.utn.backend.model.Base;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<E extends Base> extends JpaRepository<E, Long> {
    @Override
    default List<E> findAll() {
        return findAllByEliminadoFalse();
    };

    List<E> findAllByEliminadoFalse();

    default E findByIdOrThrow(Long id){
        return this.findByIdAndEliminadoFalse(id).orElseThrow(ResourceNotFoundException::new);
    }

    Optional<E> findByIdAndEliminadoFalse(Long id);

    @Transactional
    default void deleteById(Long id){
        E entity = this.findByIdOrThrow(id);
        entity.eliminado = false;
        save(entity);
    }

}
