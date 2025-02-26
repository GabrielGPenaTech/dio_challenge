package br.com.dio.service;

import br.com.dio.persistence.dao.BoardColumnDAO;
import br.com.dio.persistence.dao.BoardDAO;
import br.com.dio.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;

@AllArgsConstructor
public class BoardService {

    private Connection connection;

    public BoardEntity create(BoardEntity boardEntity) throws SQLException {
        var dao = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);

        try {
            var result = dao.insert(boardEntity);
            boardEntity.getColumns().forEach(column -> column.setBoard(boardEntity));

            for (var column : boardEntity.getColumns()) {
                boardColumnDAO.insert(column);
            }

            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }

        return boardEntity;

    }

    public boolean deleteBoard(final Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        try {
            if (!dao.exists(id)) {
                return false;
            }

            dao.delete(id);
            connection.commit();
            return true;

        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }
}
