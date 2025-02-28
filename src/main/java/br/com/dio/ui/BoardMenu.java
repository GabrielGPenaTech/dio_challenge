package br.com.dio.ui;


import br.com.dio.dto.BoardColumnInfoDTO;
import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardEntity;
import br.com.dio.persistence.entity.CardEntity;
import br.com.dio.service.BoardColumnQueryService;
import br.com.dio.service.BoardQueryService;
import br.com.dio.service.CardQueryService;
import br.com.dio.service.CardService;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.Scanner;

import static br.com.dio.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class BoardMenu {

    private final Scanner scanner = new Scanner(System.in);
    private final BoardEntity boardEntity;

    public void execute() throws SQLException {
        System.out.printf("Bem vindo ao board %s, selecione a operação desejada: \n", boardEntity.getId());
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

    private void createCard() throws SQLException {
        var card = new CardEntity();
        System.out.println("Digite o nome do titulo do card: ");
        card.setTitle(scanner.nextLine());
        System.out.println("Informe a descrição do card");
        card.setDescription(scanner.nextLine());
        card.setBoardColumn(boardEntity.getInitialColumn());

        try (var connection = getConnection()) {
            new CardService(connection).insert(card);
        }
    }

    private void moveCardToNestColumn() throws SQLException {
        System.out.println("Infome o id do card que deseja mover para próxima coluna");
        var cardId = scanner.nextLong();
        var boardsColumnsInfo = boardEntity.getColumns().stream().map(column ->
                new BoardColumnInfoDTO(column.getId(), column.getOrder(), column.getType())
        ).toList();

        try (var connection = getConnection()) {
            new CardService(connection).moveToNextColumn(cardId, boardEntity.getId(), boardsColumnsInfo);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void blockCard() throws SQLException {
        System.out.println("Informe o id do card que será bloqueado:");
        var cardId = scanner.nextLong();
        scanner.nextLine();
        System.out.println("Qual motivo do bloqueio: ");
        var blockReason = scanner.nextLine();
        var boardsColumnsInfo = boardEntity.getColumns().stream().map(column ->
                new BoardColumnInfoDTO(column.getId(), column.getOrder(), column.getType())
        ).toList();

        try (var connection = getConnection()) {
            new CardService(connection).block(cardId, boardEntity.getId(), blockReason, boardsColumnsInfo);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void unblockCard() throws SQLException {
        System.out.println("Informe o id do card que será desbloqueado:");
        var cardId = scanner.nextLong();
        scanner.nextLine();
        System.out.println("Qual motivo do desbloqueio: ");
        var unblockReason = scanner.nextLine();

        try (var connection = getConnection()) {
            new CardService(connection).unblock(cardId, boardEntity.getId(), unblockReason);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void cancelCard() throws SQLException {
        System.out.println("Informe o card deseja cancelar: ");
        var cardId = scanner.nextLong();
        var boardsColumnsInfo = boardEntity.getColumns().stream().map(column ->
                new BoardColumnInfoDTO(column.getId(), column.getOrder(), column.getType())
        ).toList();
        var cancelColumn = boardEntity.getCanceledColumn();

        try (var connection = getConnection()) {
            new CardService(connection).cancel(cardId, boardEntity.getId(), boardsColumnsInfo, cancelColumn.getId());
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void showBoard() throws SQLException {
        try (var connection = getConnection()) {
            var optional = new BoardQueryService(connection).showBoardDetails(boardEntity.getId());

            optional.ifPresent(board -> {
                System.out.printf("Board [%s, %s]\n", board.id(), board.name());
                board.columns().forEach(column ->
                        System.out.printf("Coluna [%s] tipo: [%s] tem %s cards\n",
                                column.name(), column.type(), column.cardsQuantity()));
            });

        }
    }

    private void showColumn() throws SQLException {
        var columnsIds = boardEntity.getColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumn = -1L;
        while (!columnsIds.contains(selectedColumn)) {
            System.out.printf("Escolha uma coluna do board %s\n", boardEntity.getName());
            boardEntity.getColumns().forEach(column ->
                    System.out.printf("%s - %s [%s]\n", column.getId(), column.getName(), column.getType())
            );
            selectedColumn = scanner.nextLong();
        }

        try (var connection = getConnection()) {
            var columnEntity = new BoardColumnQueryService(connection).findById(selectedColumn);
            columnEntity.ifPresent(column -> {
                System.out.printf("Coluna %s tipo %s\n", column.getName(), column.getType());

                if (column.getCards().isEmpty()) {
                    System.out.println("------- Não há cards na coluna!\n");
                } else {
                    column.getCards().forEach(card ->
                            System.out.printf("Card %s - %s\nDescrição: %s\n",
                                    card.getId(), card.getTitle() ,card.getDescription())
                    );
                }

            });
        }
    }

    private void showCard() throws SQLException {
        System.out.println("Informe o id do card que deseja visualizar: ");
        var selectedCardId = scanner.nextLong();

        try (var connection = getConnection()){
            var resultService = new CardQueryService(connection).findById(selectedCardId, boardEntity.getId());
            resultService.ifPresentOrElse(card -> {
                    System.out.printf("""
                            Card %s - %s
                            Descrição: %s
                            """, card.id(), card.title(), card.description());
                    System.out.println(card.blocked() ?
                            "Está bloqueado. Motivo: " + card.blockReason() :
                            "Não está bloqueado.");
                    System.out.printf("""
                            Já foi bloqueado %s vezes
                            Está no momento na coluna %s - %s
                            """, card.blocksAmount(), card.columnId(), card.columnName());
                },
                () -> System.out.printf("Não existe um card com id %s\n", selectedCardId)
            );
        }

    }

}
