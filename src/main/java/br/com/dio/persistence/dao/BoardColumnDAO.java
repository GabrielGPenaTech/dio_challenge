package br.com.dio.persistence.dao;

import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardColumnType;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class BoardColumnDAO {

    private Connection connection;

    public BoardColumnEntity insert(final BoardColumnEntity boardColumnEntity) throws SQLException {
        var query = "INSERT INTO boards_column(name, type, \"order\", board_id) VALUES(?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, boardColumnEntity.getName());
            preparedStatement.setString(2, boardColumnEntity.getType().name());
            preparedStatement.setInt(3, boardColumnEntity.getOrder());
            preparedStatement.setLong(4, boardColumnEntity.getBoard().getId());
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

    public List<BoardColumnEntity> findByBoardId(Long id) throws SQLException {
        List<BoardColumnEntity> list = new ArrayList<>();
        var query = "SELECT id, name,\"order\", type FROM boards_column WHERE board_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeQuery();
            var resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                var boardColumnEntity = new BoardColumnEntity();
                boardColumnEntity.setId(resultSet.getLong("id"));
                boardColumnEntity.setName(resultSet.getString("name"));
                boardColumnEntity.setOrder(resultSet.getInt("order"));
                boardColumnEntity.setType(BoardColumnType.findByName(resultSet.getString("type")));
                list.add(boardColumnEntity);
            }

            return list;
        }
    }
}
