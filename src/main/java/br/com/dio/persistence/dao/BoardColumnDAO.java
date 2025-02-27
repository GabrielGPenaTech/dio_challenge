package br.com.dio.persistence.dao;

import br.com.dio.dto.BoardColumnDTO;
import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardColumnType;
import br.com.dio.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public List<BoardColumnEntity> findByBoardId(final Long boardId) throws SQLException {
        List<BoardColumnEntity> list = new ArrayList<>();
        var query = "SELECT id, name,\"order\", type FROM boards_column WHERE board_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, boardId);
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

    public List<BoardColumnDTO> findByBoardIdWithDetails(final Long boardId) throws SQLException {
        List<BoardColumnDTO> dtos = new ArrayList<>();
        var query = """
                    SELECT
                        bc.id,
                        bc.name,
                        bc."order",
                        bc.type,
                        (SELECT COUNT(id) FROM cards WHERE board_column_id = bc.id) AS cards_quantity
                    FROM boards_column bc
                    WHERE bc.id = ?
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, boardId);
            preparedStatement.executeQuery();
            var resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                var dto = new BoardColumnDTO(
                        resultSet.getLong("bc.id"),
                        resultSet.getString("bc.name"),
                        BoardColumnType.findByName(resultSet.getString("bc.type")),
                        resultSet.getInt("cards_quantity")
                );

                dtos.add(dto);
            }

            return dtos;
        }
    }


    public Optional<BoardColumnEntity> findById(final Long id) throws SQLException {
        var query = """
                SELECT bc.name, bc.type, c.id, c.title, c.description
                FROM boards_column bc
                INNER JOIN cards c ON c.board_column_id = bc.id
                WHERE bc.id = ?
            """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeQuery();
            var resultSet = preparedStatement.getResultSet();

            if (resultSet.next()) {
                var boardColumnEntity = new BoardColumnEntity();
                boardColumnEntity.setName(resultSet.getString("bc.name"));
                boardColumnEntity.setType(BoardColumnType.findByName(resultSet.getString("bc.type")));

                do {
                    var card = new CardEntity();
                    card.setId(resultSet.getLong("c.id"));
                    card.setTitle(resultSet.getString("c.title"));
                    card.setDescription(resultSet.getString("c.description"));
                    boardColumnEntity.getCards().add(card);
                } while (resultSet.next());

                return Optional.of(boardColumnEntity);
            }

            return Optional.empty();
        }
    }
}
