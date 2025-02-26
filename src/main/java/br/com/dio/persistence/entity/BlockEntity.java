package br.com.dio.persistence.entity;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class BlockEntity {

    private Long id;
    private OffsetDateTime blockedAt;
    private System blockReason;
    private OffsetDateTime unblockedAt;
    private String unblockReason;

}
