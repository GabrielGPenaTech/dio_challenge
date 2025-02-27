package br.com.dio.service;

import br.com.dio.persistence.dao.CardDAO;
import br.com.dio.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;

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
}
