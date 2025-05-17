package src.view;

import src.model.Game;
import src.model.Player;
import src.model.utils.BoardType;
import src.model.utils.Gem;
import src.model.utils.Level;
import src.utils.Action;
import src.utils.ActionType;

import java.util.EnumMap;
import java.util.Objects;
import java.util.Scanner;

public class TerminalView implements View {
    private final Scanner scanner = new Scanner(System.in);

    public void displayGameState(Game game) {
        Objects.requireNonNull(game);
        var currentStateStringBuilder = new StringBuilder();
        currentStateStringBuilder
                .append("État du jeu :\n")
                .append("Tour du joueur n° ")
                .append(game.currentPlayerIndex())
                .append("\n")
                .append(game.toString());
        System.out.println(currentStateStringBuilder.toString());
    }

    public BoardType getBoardType() {
        BoardType boardType = null;
        do {
            System.out.println("Type de plateau (simple | complet) : ");
            String userInput = scanner.nextLine();
            try {
                boardType = BoardType.getBoardTypeFromString(userInput);
            } catch (IllegalArgumentException e) {
                System.out.println("Type de plateau invalide, veuillez réessayer.");
            }
        } while (boardType == null);
        return boardType;
    }

    public int getNumberOfPlayers() {
        while (true) {
            System.out.println("Nombre de joueurs : ");
            try {
                String userInput = scanner.nextLine();
                return Integer.parseInt(userInput);
            } catch (Exception e) {
                System.out.println("Nombre de joueurs invalide, veuillez réessayer.");
            }
        }
    }

    public Action readPlayerAction(Player player) {
        Objects.requireNonNull(player);
        System.out.println("Action (take <red | green ...> | buy <level> <index> | reserve <level> <index> | buy_reserved <index> ): ");

        Action action = null;
        do {
            String userInput = scanner.nextLine();
            String[] parts = userInput.trim().split("\\s+");
            String command = parts[0].toLowerCase();
            try {
                action = switch (command) {
                    case "take" -> handleTakeAction(parts);
                    case "buy" -> handleBuyAction(parts);
                    case "reserve" -> handleReserveAction(parts);
                    case "buy_reserved" -> handleBuyReservedAction(parts);
                    default -> throw new IllegalArgumentException();
                };
            } catch (Exception e) {
                System.out.println("Erreur de saisie, veuillez réessayer.");
            }
        } while (action == null);

        return action;
    }

    private Action handleBuyReservedAction(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Veuillez spécifier l'index de la carte réservée à acheter.");
            return null;
        }

        while (true) {
            System.out.println("Nombre de joueurs : ");
            try {
                String userInput = scanner.nextLine();
                int index = Integer.parseInt(userInput);
                return new Action(ActionType.BUY_RESERVED, index);
            } catch (Exception e) {
                System.out.println("Numéro entré invalide .");
            }
        }
    }

    @Override
    public void displayMessage(String message) {
        Objects.requireNonNull(message);

        System.out.println(message);
    }

    private Action handleReserveAction(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Veuillez spécifier le niveau et l'index de la carte à réserver.");
            return null;
        }
        Level level;
        int index;
        try {
            level = Level.getLevelFromInt(Integer.parseInt(parts[1]));
            index = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            System.out.println("Veuillez spécifier un niveau et un index valides.");
            return null;
        }

        return new Action(ActionType.RESERVE, index, level);
    }

    private Action handleBuyAction(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Veuillez spécifier le niveau et l'index de la carte à acheter.");
            return null;
        }
        Level level;
        int index;
        try {
            level = Level.getLevelFromInt(Integer.parseInt(parts[1]));
            index = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            System.out.println("Veuillez spécifier un niveau et un index valides.");
            return null;
        }

        return new Action(ActionType.BUY, index, level);
    }

    private Action handleTakeAction(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Veuillez spécifier la couleur de gemme à prendre.");
            return null;
        }
        var gems = new EnumMap<Gem, Integer>(Gem.class);
        for (int i = 1; i < parts.length; i++) {
            gems.merge(Gem.getGemFromColor(parts[i]), 1, Integer::sum);
        }



        return new Action(ActionType.TAKE, gems);
    }
}
