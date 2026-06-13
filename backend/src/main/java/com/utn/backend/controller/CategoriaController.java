package com.utn.backend.controller;

import com.utn.backend.dto.CategoriaRequestDTO;
import com.utn.backend.dto.CategoriaResponseDTO;
import com.utn.backend.service.impl.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/categoria")
@RequiredArgsConstructor
public class CategoriaController {
    private final CategoriaService categoriaService;

    @PostMapping
    public CategoriaResponseDTO create(@Valid @RequestBody CategoriaRequestDTO requestDTO) {
        return categoriaService.create(requestDTO);
    };

}
