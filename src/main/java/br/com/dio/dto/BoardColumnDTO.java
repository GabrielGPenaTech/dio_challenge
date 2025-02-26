package br.com.dio.dto;

import br.com.dio.persistence.entity.BoardColumnType;

public record BoardColumnDTO(
        Long id,
        String name,
        BoardColumnType type,
        int cardsQuantity
) {}
