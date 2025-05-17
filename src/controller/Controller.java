package src.controller;

import src.model.Game;
import src.model.Player;
import src.model.cards.Card;
import src.utils.Action;
import src.view.TerminalView;
import src.view.View;
import java.util.Objects;

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
            boolean valid;
            do {
                action = view.readPlayerAction(game.getCurrentPlayer());
                valid = handleAction(action);
            } while (!valid);
            game.nextTurn();
        }
        view.displayGameState(game);
        view.displayMessage("Fin de la partie ! Vainqueur : Joueur n°" + game.getWinner());
    }

    private boolean handleAction(Action action) {
        Objects.requireNonNull(action);
        Player player = game.getCurrentPlayer();

        try {
            switch (action.type()) {
                case TAKE -> {
                    player.takeGems(action.gems(), game.board().gemBank());
                }
                case BUY -> {
                    var lvl = action.cardLevel();
                    int idx = action.cardIndex();

                    var card = game.board().getCard(lvl,idx);
                    player.purchaseCard(card, game.board().gemBank());
                    game.board().removeCard(lvl, idx);
                }
                case RESERVE -> {
                    var lvl = action.cardLevel();
                    int idx = action.cardIndex();
                    Card card = game.board().getCard(lvl,idx);
                    player.reserveCard(card);
                    game.board().removeCard(lvl, idx);
                }
                case BUY_RESERVED -> {
                    var reservedCard = player.reservedCards().get(action.cardIndex());
                    player.purchaseCard(reservedCard, game.board().gemBank());
                    player.removeReservedCard(action.cardIndex());
                }
                default -> throw new IllegalArgumentException("Action inconnue");
            }
            return true;
        } catch (IllegalArgumentException e) {
            view.displayMessage("Action invalide: " + e.getMessage());
            return false;
        }
    }
}
