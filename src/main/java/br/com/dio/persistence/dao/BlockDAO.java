package br.com.dio.persistence.dao;

import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@AllArgsConstructor
public class BlockDAO {

    private final Connection connection;

    public void insert(final String reason, final Long cardId) throws SQLException {
        var query = "INSERT INTO blocks (block_reason, card_id) VALUES (?, ?)";
        try (var preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, reason);
            preparedStatement.setLong(2, cardId);
            preparedStatement.executeUpdate();
        }
    }
}
