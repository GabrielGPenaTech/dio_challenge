package br.com.dio.persistence.dao;

import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import static br.com.dio.utils.OffSetDateTimeConverter.toTimestamp;

@AllArgsConstructor
public class BlockDAO {

    private final Connection connection;

    public void insert(final String reason, final Long cardId) throws SQLException {
        var query = "INSERT INTO blocks (block_reason, card_id, block_at) VALUES (?, ?, ?)";
        try (var preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, reason);
            preparedStatement.setLong(2, cardId);
            preparedStatement.setTimestamp(3, toTimestamp(OffsetDateTime.now()));
            preparedStatement.executeUpdate();
        }
    }

    public void unblockCard(final String reason, final Long cardId) throws SQLException {
        var query = "UPDATE blocks SET unblock_reason = ?, unblock_at = ? WHERE card_id = ? AND unblock_reason IS NULL";
        try (var preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, reason);
            preparedStatement.setTimestamp(2, toTimestamp(OffsetDateTime.now()));
            preparedStatement.setLong(3, cardId);
            preparedStatement.executeUpdate();
        }

    }
}
