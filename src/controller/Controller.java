package src.controller;

import src.model.Game;
import src.utils.Action;
import src.utils.DropTokensAction;
import src.view.TerminalView;
import src.view.View;

import java.util.Objects;

public class Controller {
    private final Game game;
    private final View view;

    public Controller(Game game, View view) {
        Objects.requireNonNull(game);
        Objects.requireNonNull(view);
        this.game = game;
        this.view = view;
    }

    private void handleUserAction() {
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
    }

    public void start() {
        while (!game.isGameOver()) {
            view.displayGameState(game);
            handleUserAction();
            handlePlayerTooManyGems();
            game.nextTurn();
        }

        view.displayWin(game.getWinner());
    }

    private void handlePlayerTooManyGems() {
        if (!game.currentPlayerHasTooManyGems()) return;
        var isValid = false;
        DropTokensAction dropAction;
        do {
            dropAction = view.readDropTokensAction(game.getCurrentPlayer());
            if (game.isValidAction(dropAction)) {
                isValid = true;
            } else {
                view.invalidActionMessage(dropAction);
            }
        }while (!isValid);
        game.handleAction(dropAction);
    }

    public static Controller createController() {
        var view = new TerminalView();
        var boardType = view.getBoardType();
        int numberOfPlayers;

        while (true) {
            numberOfPlayers = view.getNumberOfPlayers();
            if (Game.isValidNumberOfPlayers(numberOfPlayers)) {
                var game = Game.createGame(numberOfPlayers, boardType);
                return new Controller(game, view);
            }
            view.invalidNumberOfPlayersMessage(numberOfPlayers);
        }
    }
}
