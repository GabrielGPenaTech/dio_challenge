package br.com.dio.ui;


import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardEntity;
import br.com.dio.service.BoardColumnQueryService;
import br.com.dio.service.BoardQueryService;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.Scanner;

import static br.com.dio.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class BoardMenu {

    private final Scanner scanner = new Scanner(System.in);
    private final BoardEntity boardEntity;

    public void execute() throws SQLException {
        System.out.printf("Bem vindo ao board %s, selecione a operação desejada: ", boardEntity.getId());
        var option = -1;
        while (option != 9) {
            System.out.println("1 - Criar um card");
            System.out.println("2 - Mover um card");
            System.out.println("3 - Bloquear um card");
            System.out.println("4 - Desbloquear um card");
            System.out.println("5 - Cancelar um card");
            System.out.println("6 - Ver board");
            System.out.println("7 - Ver os cards da coluna");
            System.out.println("8 - Ver o card");
            System.out.println("9 - Voltar ao menu anterior");
            System.out.println("10 - Sair");
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> createCard();
                case 2 -> moveCardToNestColumn();
                case 3 -> blockCard();
                case 4 -> unblockCard();
                case 5 -> cancelCard();
                case 6 -> showBoard();
                case 7 -> showColumn();
                case 8 -> showCard();
                case 9 -> System.out.println("Voltando ao menu anterior");
                case 10 -> System.exit(0);
                default -> System.out.println("Opção inválida, informe uma opção do menu!");
            }
        }
    }

    private void createCard() {
    }

    private void moveCardToNestColumn() {
    }

    private void blockCard() {
    }

    private void unblockCard() {
    }

    private void cancelCard() {
    }

    private void showBoard() throws SQLException {
        try (var connection = getConnection()) {
            var optional = new BoardQueryService(connection).showBoardDetails(boardEntity.getId());

            optional.ifPresent(board -> {
                System.out.printf("Board [%s, %s]\n", board.id(), board.name());
                board.columns().forEach(column -> {
                    System.out.printf("Coluna [%s] tipo: [%s] tem %s cards\n", column.name(), column.type(), column.cardsQuantity());
                });
            });

        }
    }

    private void showColumn() throws SQLException {
        var columnsIds = boardEntity.getColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumn = -1L;
        while (!columnsIds.contains(selectedColumn)) {
            System.out.printf("Escolha uma coluna do board %s\n", boardEntity.getName());
            boardEntity.getColumns().forEach(column ->
                    System.out.printf("%s - %s [%s]", column.getId(), column.getName(), column.getType())
            );
            selectedColumn = scanner.nextLong();
        }

        try (var connection = getConnection()) {
            var columnEntity = new BoardColumnQueryService(connection).findById(selectedColumn);
            columnEntity.ifPresent(column -> {
                System.out.printf("Coluna %s tipo %s\n", column.getName(), column.getType());
                column.getCards().forEach(card ->
                        System.out.printf("Card %s - %s\nDescrição: %s",
                                card.getId(), card.getTitle() ,card.getDescription())
                );
            });
        }
    }

    private void showCard() {
    }

}
