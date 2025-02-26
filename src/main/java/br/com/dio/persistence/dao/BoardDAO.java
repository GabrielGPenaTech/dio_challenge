package br.com.dio.persistence.dao;

import br.com.dio.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class BoardDAO {

    private Connection connection;

    private BoardEntity insert(final BoardEntity boardEntity) throws SQLException {
        var query = "INSER INTO board (name) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, boardEntity.getName());
            statement.executeUpdate();
            var result = statement.getGeneratedKeys();
            if (result != null && result.next()) {
                boardEntity.setId(result.getLong("id"));
            }

            return boardEntity;
        }
    }

    private void delete(final Long id) throws SQLException {
        var query = "DELETE FROM boards WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    private Optional<BoardEntity> findById(final Long id) throws SQLException {
        var query = "SELECT id, name FROM boards WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();

            if (resultSet.next()) {
                var boardEntity = new BoardEntity();
                boardEntity.setId(resultSet.getLong("id"));
                boardEntity.setName(resultSet.getString("name"));
                return Optional.of(boardEntity);
            }

            return Optional.empty();
        }
    }

    private boolean exists(final Long id) throws SQLException {
        var query = "SELECT 1 FROM boards WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            statement.executeQuery();
            return  statement.getResultSet().next();
        }
    }
}
