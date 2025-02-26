package br.com.dio.dto;

import br.com.dio.persistence.entity.BoardColumnType;

import java.util.List;

public record BoardDetailsDTO(Long id, String name, List<BoardColumnDTO> columns) {}
