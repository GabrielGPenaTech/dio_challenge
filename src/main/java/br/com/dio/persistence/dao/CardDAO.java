package br.com.dio.persistence.dao;

import br.com.dio.dto.CardDetailsDTO;
import br.com.dio.utils.OffSetDateTimeConverter;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class CardDAO {

    private final Connection connection;

    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        var query = """
                SELECT
                     c.id,
                     c.title,
                     c.description,
                     b.block_at,
                     b.block_reason,
                     c.board_column_id,
                     bc.name,
                     (SELECT COUNT(id) FROM blocks WHERE card_id = c.id) as block_quantity
                FROM cards c
                LEFT JOIN blocks b ON c.id = b.card_id AND b.unblock_at IS NULL
                INNER JOIN boards_column bc ON bc.id = c.board_column_id
                WHERE c.id = ?
        """;

        try (var statement = connection.prepareStatement(query) ) {
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if (resultSet.next()) {
                var dto = new CardDetailsDTO(
                        resultSet.getLong("c.id"),
                        resultSet.getString("c.title"),
                        resultSet.getString("c.description"),
                        OffSetDateTimeConverter.of(resultSet.getTimestamp("b.block_at")),
                        resultSet.getString("b.block_reason"),
                        resultSet.getInt("block_quantity"),
                        resultSet.getLong("board_column_id"),
                        resultSet.getString("bc.name"),
                        !resultSet.getString("b.block_reason").isEmpty()
                );

                return Optional.of(dto);
            }
        }


        return Optional.empty();
    }
}
