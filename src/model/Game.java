package src.model;

import src.model.utils.BoardType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Game {
    private final Board board;
    private final List<Player> players;
    private int currentPlayerIndex;
    private boolean gameOver;

    public Game(int nbPlayers, BoardType boardType) {
        if (nbPlayers < 1 || nbPlayers > 4) throw new IllegalArgumentException();
        currentPlayerIndex = 0;
        gameOver = false;
        players = new ArrayList<>();
        board = new Board(nbPlayers, boardType);

        for (int i = 0; i < nbPlayers; i++) {
            players.add(new Player());
        }
    }

    public void nextTurn() {
        if (gameOver) return;

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

        if (currentPlayerIndex == 0 && players.stream().anyMatch(p -> p.getPoints() >= 15)) {
            gameOver = true;
        }
    }

    public Player getWinner() {
        if (!gameOver) return null;
        return players.stream()
                .filter(player -> player.getPoints() >= 15)
                .min(Comparator.comparingInt(player -> player.getCards().size()))
                .orElse(null);
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public Board getBoard() {
        return board;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
