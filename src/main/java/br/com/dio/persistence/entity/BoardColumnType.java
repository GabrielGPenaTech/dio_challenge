package br.com.dio.persistence.entity;

import java.util.stream.Stream;

public enum BoardColumnType {
    INITIAL ,FINAL, CANCEL, PENDING;

    public static BoardColumnType findByName(final String name) {
        return Stream.of(BoardColumnType.values())
                .filter(type -> type.name().equals(name))
                .findFirst().orElseThrow();
    }
}
