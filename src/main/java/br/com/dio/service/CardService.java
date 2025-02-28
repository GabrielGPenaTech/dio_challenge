package br.com.dio.service;

import br.com.dio.dto.BoardColumnInfoDTO;
import br.com.dio.exception.*;
import br.com.dio.persistence.dao.BlockDAO;
import br.com.dio.persistence.dao.CardDAO;
import br.com.dio.persistence.entity.BoardColumnType;
import br.com.dio.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@AllArgsConstructor
public class CardService {

    private final Connection connection;

    public CardEntity insert(CardEntity cardEntity) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            dao.insert(cardEntity);
            connection.commit();
            return cardEntity;

        }catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    public void moveToNextColumn(final Long cardId, final Long boardId ,final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var currentColumn = isPossibleToMove(cardId, boardId, boardColumnsInfo, dao);

            var nextColumn = currentColumn.order() + 1L;

            dao.moveToColumn(cardId, nextColumn);
            connection.commit();

        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    public void cancel(
            final Long cardId,
            final Long boardId ,
            final List<BoardColumnInfoDTO> boardColumnsInfo,
            final Long cancelColumnId
    ) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var currentColumn = isPossibleToMove(cardId, boardId, boardColumnsInfo, dao);

            dao.moveToColumn(cardId, cancelColumnId);
            connection.commit();

        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    public void block(
            final Long cardId,
            final Long boardId,
            final String reason,
            final List<BoardColumnInfoDTO> boardColumnsInfo
    ) throws SQLException {

        try {
            var dao = new CardDAO(connection);
            var blockDao = new BlockDAO(connection);
            var currentColumn = isPossibleToMove(cardId, boardId, boardColumnsInfo, dao);

            blockDao.insert(reason, cardId);
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    private BoardColumnInfoDTO isPossibleToMove(
            Long cardId,
            Long boardId,
            List<BoardColumnInfoDTO> boardColumnsInfo,
            CardDAO dao
    ) throws SQLException {
        var optional = dao.findById(cardId, boardId);
        var dto = optional.orElseThrow(() -> new EntityNotFoundException("Card not found"));

        if (dto.blocked()) {
            throw new CardBlockedException("Action not possible because card is blocked");
        }

        var currentColumn = boardColumnsInfo.stream().filter(column ->
                        column.id().equals(dto.columnId()))
                .findFirst().orElseThrow(() -> new EntityNotFoundException("Column not found"));

        if (currentColumn.columnType().equals(BoardColumnType.FINAL)) {
            throw new CardFinishedException("Action not possible because card is finished");
        }

        if (currentColumn.columnType().equals(BoardColumnType.CANCEL)) {
            throw new CardCancelledException("Action not possible because card is cancelled");
        }


        return currentColumn;
    }
}
