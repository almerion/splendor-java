package src.model;

import src.model.utils.BoardType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Game {
    private final Board board;
    private final List<Player> players;
    private int currentPlayerIndex;
    private int finalRoundPlayerIndex;
    private boolean gameOver;

    public Game(int nbPlayers, BoardType boardType) {
        if (nbPlayers < 1 || nbPlayers > 4) throw new IllegalArgumentException();
        currentPlayerIndex = 0;
        gameOver = false;
        finalRoundPlayerIndex = -1;
        players = new ArrayList<>();
        board = new Board(nbPlayers, boardType);

        for (int i = 0; i < nbPlayers; i++) {
            players.add(new Player());
        }
    }

    public void nextTurn() {
        if (gameOver) return;

        // Quand un joueur atteint 15 points, on laisse le tour de table se finir.
        if (players.get(currentPlayerIndex).getPoints() >= 15) {
            finalRoundPlayerIndex = currentPlayerIndex;
        }

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

        if (currentPlayerIndex == finalRoundPlayerIndex) {
            gameOver = true;
        }
    }

    public Player getWinner() {
        if (!gameOver) return null;
        return players.stream()
                .filter(player -> player.getPoints() >= 15)
                .min(Comparator.comparingInt(player -> player.cards().size()))
                .orElse(null);
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public int currentPlayerIndex() {
        return currentPlayerIndex;
    }

    public Board board() {
        return board;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("État de la partie :\n");
        sb.append("Joueurs :\n");
        for (int i = 0; i < players.size(); i++) {
            sb.append("Joueur ").append(i + 1).append(": ").append(players.get(i).toString()).append("\n");
        }
        sb.append("Joueur courant : ").append((currentPlayerIndex + 1) % players.size()).append("\n");
        sb.append("Plateau :\n");
        sb.append(board.toString()).append("\n");

        return sb.toString();
    }
}
