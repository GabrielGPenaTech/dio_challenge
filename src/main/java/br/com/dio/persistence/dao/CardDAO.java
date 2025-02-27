package br.com.dio.persistence.dao;

import br.com.dio.dto.CardDetailsDTO;
import br.com.dio.persistence.entity.CardEntity;
import br.com.dio.utils.OffSetDateTimeConverter;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static java.util.Objects.nonNull;

@AllArgsConstructor
public class CardDAO {

    private final Connection connection;

    public CardEntity insert(final CardEntity cardEntity) throws SQLException {
        var query = "INSERT INTO cards (title, description, board_column_id) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, cardEntity.getTitle());
            preparedStatement.setString(2, cardEntity.getDescription());
            preparedStatement.setLong(3, cardEntity.getBoardColumn().getId());
            preparedStatement.executeUpdate();

            try (var result = preparedStatement.getGeneratedKeys()) {
                if (result != null && result.next()) {
                    cardEntity.setId(result.getLong(1));
                }
            }
            return cardEntity;
        }
    }

    public Optional<CardDetailsDTO> findById(final Long id, final Long boardId) throws SQLException {
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
                WHERE c.id = ? AND bc.board_id = ?
        """;

        try (var statement = connection.prepareStatement(query) ) {
            statement.setLong(1, id);
            statement.setLong(2, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if (resultSet.next()) {
                var dto = new CardDetailsDTO(
                        resultSet.getLong("id"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        OffSetDateTimeConverter.of(resultSet.getTimestamp("block_at")),
                        resultSet.getString("block_reason"),
                        resultSet.getInt("block_quantity"),
                        resultSet.getLong("board_column_id"),
                        resultSet.getString("name"),
                        nonNull(resultSet.getString("block_reason"))
                );

                return Optional.of(dto);
            }
        }


        return Optional.empty();
    }
}
