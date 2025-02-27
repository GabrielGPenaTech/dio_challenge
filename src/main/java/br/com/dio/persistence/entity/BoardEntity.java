package br.com.dio.persistence.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
public class BoardEntity {

    private Long id;
    private String name;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<BoardColumnEntity> columns = new ArrayList<>();

    public BoardColumnEntity getInitialColumn() {
        return columns.stream().filter(column ->
                column.getType().equals(BoardColumnType.INITIAL))
                .findFirst().orElseThrow();
    }
}
