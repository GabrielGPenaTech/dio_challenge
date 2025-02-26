package br.com.dio.service;

import br.com.dio.dto.BoardDetailsDTO;
import br.com.dio.persistence.dao.BoardColumnDAO;
import br.com.dio.persistence.dao.BoardDAO;
import br.com.dio.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class BoardQueryService {

    private Connection connection;

    public Optional<BoardEntity> findById(Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);
        var optional = dao.findById(id);

        if (optional.isPresent()) {
            var boardEntity = optional.get();
            boardEntity.setColumns(boardColumnDAO.findByBoardId(boardEntity.getId()));
            return Optional.of(boardEntity);
        }

        return Optional.empty();
    }

    public Optional<BoardDetailsDTO> showBoardDetails(Long id) throws SQLException {
        var boardDAO = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);
        var optional = boardDAO.findById(id);

        if (optional.isPresent()) {
            var boardEntity = optional.get();
            var columns = boardColumnDAO.findByBoardIdWithDetails(boardEntity.getId());
            var dto = new BoardDetailsDTO(boardEntity.getId(), boardEntity.getName(), columns);
            return Optional.of(dto);
        }

        return Optional.empty();
    }
}
