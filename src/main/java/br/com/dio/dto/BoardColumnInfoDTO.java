package br.com.dio.dto;

import br.com.dio.persistence.entity.BoardColumnType;

public record BoardColumnInfoDTO(Long id, int order, BoardColumnType columnType) { }
