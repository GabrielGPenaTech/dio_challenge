package br.com.dio.persistence.dao;

import br.com.dio.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@AllArgsConstructor
public class BoardDAO {

    private Connection connection;

    public BoardEntity insert(final BoardEntity boardEntity) throws SQLException {
        var query = "INSERT INTO boards (nome) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, boardEntity.getName());
            statement.executeUpdate();
            try (var result = statement.getGeneratedKeys()) {
                if (result != null && result.next()) {
                    boardEntity.setId(result.getLong(1));
                }
            }
            return boardEntity;
        }
    }

    public void delete(final Long id) throws SQLException {
        var query = "DELETE FROM boards WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    public Optional<BoardEntity> findById(final Long id) throws SQLException {
        var query = "SELECT id, nome FROM boards WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();

            if (resultSet.next()) {
                var boardEntity = new BoardEntity();
                boardEntity.setId(resultSet.getLong("id"));
                boardEntity.setName(resultSet.getString("nome"));
                return Optional.of(boardEntity);
            }

            return Optional.empty();
        }
    }

    public boolean exists(final Long id) throws SQLException {
        var query = "SELECT 1 FROM boards WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            statement.executeQuery();
            return  statement.getResultSet().next();
        }
    }
}
