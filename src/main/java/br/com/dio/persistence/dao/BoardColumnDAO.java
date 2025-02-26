package br.com.dio.persistence.dao;

import br.com.dio.persistence.entity.BoardColumnEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@AllArgsConstructor
public class BoardColumnDAO {

    private Connection connection;

    public BoardColumnEntity insert(final BoardColumnEntity boardColumnEntity) throws SQLException {
        var query = "INSERT INTO boards_column(name, type, "order", board_id) VALUES(?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, boardColumnEntity.getName());
            preparedStatement.setString(2, boardColumnEntity.getType().name());
            preparedStatement.setInt(3, boardColumnEntity.getOrder());
            preparedStatement.setLong(4, boardColumnEntity.getBoardEntity().getId());
            preparedStatement.executeUpdate();

            if (preparedStatement.getUpdateCount() == 0) {
                throw new SQLException();
            }

            var result = preparedStatement.getGeneratedKeys();
            if (result.next()) {
                boardColumnEntity.setId(result.getLong(1));
            }

            return boardColumnEntity;
        }
    }
}
