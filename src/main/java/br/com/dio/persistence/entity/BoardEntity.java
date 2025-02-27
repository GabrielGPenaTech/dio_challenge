package br.com.dio.persistence.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Data
public class BoardEntity {

    private Long id;
    private String name;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<BoardColumnEntity> columns = new ArrayList<>();

    public BoardColumnEntity getInitialColumn() {
       return getFilteredColumn(column -> column.getType().equals(BoardColumnType.INITIAL));
    }

    public BoardColumnEntity getCanceledColumn() {
        return getFilteredColumn(column -> column.getType().equals(BoardColumnType.CANCEL));
    }


    private BoardColumnEntity getFilteredColumn(Predicate<BoardColumnEntity> filter) {
        return columns.stream().filter(filter)
                .findFirst().orElseThrow();
    }
}
