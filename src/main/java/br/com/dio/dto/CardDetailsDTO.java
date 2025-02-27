package br.com.dio.dto;

import java.time.OffsetDateTime;

public record CardDetailsDTO(
        Long id,
        String title,
        String description,
        OffsetDateTime blockedAt,
        String blockReason,
        int blocksAmount,
        Long columnId,
        String columnName,
        Boolean blocked
) {}
