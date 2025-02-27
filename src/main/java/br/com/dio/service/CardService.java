package br.com.dio.service;

import br.com.dio.dto.BoardColumnInfoDTO;
import br.com.dio.exception.CardBlockedException;
import br.com.dio.exception.CardCanceledException;
import br.com.dio.exception.CardFinishedException;
import br.com.dio.exception.EntityNotFoundException;
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

            var nextColumn = boardColumnsInfo.stream().filter(column ->
                    column.order() == currentColumn.order() + 1)
                    .findFirst().orElseThrow(() -> new EntityNotFoundException("The card is cancelled"));


            dao.moveToColumn(cardId, nextColumn.id());
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

            if (currentColumn.columnType().equals(BoardColumnType.CANCEL)) {
                throw new CardCanceledException("Card is already cancelled");
            }

            dao.moveToColumn(cardId, cancelColumnId);
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
            throw new CardBlockedException("Card is blocked");
        }

        var currentColumn = boardColumnsInfo.stream().filter(column ->
                        column.id().equals(dto.columnId()))
                .findFirst().orElseThrow(() -> new EntityNotFoundException("Column not found"));

        if (currentColumn.columnType().equals(BoardColumnType.FINAL)) {
            throw new CardFinishedException("Card is final");
        }

        return currentColumn;
    }
}
