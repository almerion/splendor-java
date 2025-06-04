package src.controller;

import src.model.Game;
import src.utils.Action;
import src.view.TerminalView;
import src.view.View;

public class Controller {
    private final Game game;
    private final View view;

    public Controller() {
        this.view = new TerminalView();
        var boardType = view.getBoardType();
        var hasThrown = false;
        Game createdGame = null;
        do {
            var numberOfPlayers = view.getNumberOfPlayers();
            try {
                createdGame = new Game(numberOfPlayers, boardType);
                hasThrown = false;
            } catch (IllegalArgumentException e) {
                System.out.println("Nombre de joueurs invalide, veuillez réessayer.");
                hasThrown = true;
            }
        }while (hasThrown);

        this.game = createdGame;
    }

    public void start() {
        while (!game.isGameOver()) {
            view.displayGameState(game);
            Action action;
            var isValid = false;
            do {
                action = view.readPlayerAction(game.getCurrentPlayer());
                if (!game.isValidAction(action)) {
                    view.invalidActionMessage(action);
                } else {
                    isValid = true;
                }
            } while (!isValid);
            game.handleAction(action);
            game.nextTurn();
        }

        view.displayWin(game.getWinner());
    }
}
