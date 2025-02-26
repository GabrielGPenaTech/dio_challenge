package br.com.dio.ui;

import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardColumnType;
import br.com.dio.persistence.entity.BoardEntity;
import br.com.dio.service.BoardQueryService;
import br.com.dio.service.BoardService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static br.com.dio.persistence.config.ConnectionConfig.getConnection;

public class MainMenu {

    private final Scanner scanner = new Scanner(System.in);

    public void execute() throws SQLException {
        System.out.println("Bem vindo ao gerenciador de boards, escolha uma opção:");
        var option = -1;
        while (true) {
            System.out.println("1 - Criar um novo board");
            System.out.println("2 - Selecionar um board existente");
            System.out.println("3 - Excluir um board");
            System.out.println("4 - Sair");
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> createBoard();
                case 2 -> selectBoard();
                case 3 -> deleteBoard();
                case 4 -> System.exit(0);
                default -> System.out.println("Opção inválida, informe uma opção do menu!");
            }
        }
    }

    private void createBoard() throws SQLException {
        var board = new BoardEntity();
        System.out.println("Digite o nome do titulo do board: ");
        var name = scanner.nextLine();
        board.setName(name);

        System.out.println("Seu board terá mais do que as 3 colunas padrão ? se sim informe quantas, se não digite '0'");
        var quantityExtraColumns = scanner.nextInt();
        scanner.nextLine();

        List<BoardColumnEntity> boardColumns = new ArrayList<>();

        System.out.println("Informe o nome da coluna inicial do board: ");
        var firstColumnName = scanner.nextLine();
        var initialColumn =  createBoardColumn(firstColumnName, 0, BoardColumnType.INITIAL);
        boardColumns.add(initialColumn);

        for(int i = 1; i <= quantityExtraColumns; i++) {
            System.out.println("Informe o nome da coluna de tarefa pendente: ");
            var pendingColumnName = scanner.nextLine();
            var pendingColumn =  createBoardColumn(pendingColumnName, i++, BoardColumnType.PENDING);
            boardColumns.add(pendingColumn);
        }

        System.out.println("Informe o nome da coluna final do board: ");
        var finalColumnName = scanner.nextLine();
        var finalColumn =  createBoardColumn(finalColumnName, quantityExtraColumns + 1, BoardColumnType.FINAL);
        boardColumns.add(finalColumn);

        System.out.println("Informe o nome da coluna de cancelamento: ");
        var cancelColumnName = scanner.nextLine();
        var cancelColumn =  createBoardColumn(cancelColumnName, quantityExtraColumns + 2, BoardColumnType.CANCEL);
        boardColumns.add(cancelColumn);
        board.setColumns(boardColumns);

        try(var connection = getConnection()) {
            var service = new BoardService(connection);
            service.create(board);
        }

    }

    private void selectBoard() throws SQLException {
        System.out.println("Informe o id do board que deseja selecionar: ");
        var id = scanner.nextLong();

        try(var connection = getConnection()) {
            var service = new BoardQueryService(connection);
            var optional = service.findById(id);

            if(optional.isPresent()) {
                var boardMenu = new BoardMenu(optional.get());
                boardMenu.execute();
            } else {
                System.out.println("Não foi possível achar o board que deseja selecionar!");
            }
        }
    }

    private void deleteBoard() throws SQLException {
        System.out.println("Digite um id do board: ");
        var id = scanner.nextLong();
        try(var connection = getConnection()) {
            var service = new BoardService(connection);
            if (service.deleteBoard(id)) {
                System.out.println("Board deletado com sucesso!");
            } else {
                System.out.println("Não foi possivel excluir o board!");
            }
        }
    }

    private BoardColumnEntity createBoardColumn(final String name, final int order, final BoardColumnType type) {
        var boardColumnEntity = new BoardColumnEntity();
        boardColumnEntity.setName(name);
        boardColumnEntity.setOrder(order);
        boardColumnEntity.setType(type);
        return boardColumnEntity;
    }
}
