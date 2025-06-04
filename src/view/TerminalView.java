package src.view;

import src.model.Game;
import src.model.GemBank;
import src.model.Player;
import src.model.utils.BoardType;
import src.model.utils.Gem;
import src.model.utils.Level;
import src.utils.*;

import java.util.EnumMap;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;

public class TerminalView implements View {
    private final Scanner scanner = new Scanner(System.in);
    private final static Pattern NUMBER_PATTERN = Pattern.compile("\\d+");

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
        while (true) {
            System.out.println("Type de plateau (simple | complet) : ");
            String userInput = scanner.nextLine();
            if (BoardType.isValidBoardType(userInput)) {
                return BoardType.getBoardTypeFromString(userInput);
            }
            System.out.println("Type de plateau invalide, veuillez réessayer.");
        }
    }

    public int getNumberOfPlayers() {
        while (true) {
            System.out.print("Nombre de joueurs : ");
            var userInput = scanner.nextLine().trim();
            if (NUMBER_PATTERN.matcher(userInput).matches()) {
                return Integer.parseInt(userInput);
            }
            System.out.println("Veuillez entrer un nombre valide.");
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
            action = switch (command) {
                case "take" -> handleTakeAction(parts);
                case "buy" -> handleBuyAction(parts);
                case "reserve" -> handleReserveAction(parts);
                case "buy_reserved" -> handleBuyReservedAction(parts);
                default -> {;
                    System.out.println("Action invalide, veuillez réessayer.");
                    yield null;
                }
            };
        } while (action == null);

        return action;
    }

    public void invalidActionMessage(Action action) {
        System.out.println("Action invalide, veuillez réessayer.");
    }

    public void displayWin(Player player) {
        System.out.println("Fin de la partie ! Vainqueur : " + player);
    }

    public DropTokensAction readDropTokensAction(Player currentPlayer) {
        System.out.println("Vous avez trop de jetons, veuillez en déposer.");
        DropTokensAction action = null;
        do {
            System.out.println("Action de dépôt de jetons (drop <red | green ...> <red | green ...) ne pas déposer plus de jetons que nécessaire, pour rappel vous devez en avoir 10 maximum");
            String userInput = scanner.nextLine();
            String[] parts = userInput.trim().split("\\s+");
            action = handleDropTokensAction(parts);
        }while (action == null);
        return action;
    }

    public void invalidNumberOfPlayersMessage(int numberOfPlayers) {
        System.out.println("Nombre de joueurs invalide : " + numberOfPlayers + ". Veuillez entrer un nombre valide (2-4).");
    }

    private DropTokensAction handleDropTokensAction(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Veuillez spécifier au moins une couleur de gemme à déposer.");
            return null;
        }
        var gemsToDrop = new EnumMap<Gem, Integer>(Gem.class);
        for (int i = 1; i < parts.length; i++) {
            if (!Gem.isValidGem(parts[i])) {
                System.out.println("Couleur de gemme invalide : " + parts[i]);
                return null;
            }
            Gem gem = Gem.getGemFromColor(parts[i]);
            gemsToDrop.merge(gem, 1, Integer::sum);
        }
        return new DropTokensAction(new GemBank(gemsToDrop));
    }

    private Action handleBuyReservedAction(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Veuillez spécifier l'index de la carte réservée à acheter.");
            return null;
        }

        if (!NUMBER_PATTERN.matcher(parts[1]).matches()) {
            System.out.println("Index invalide, veuillez entrer un nombre.");
            return null;
        }
        var index = Integer.parseInt(parts[1]);
        return new BuyReservedAction(index);
    }

    private Action handleReserveAction(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Veuillez spécifier le niveau et l'index de la carte à réserver.");
            return null;
        }
        if (!NUMBER_PATTERN.matcher(parts[1]).matches() || !NUMBER_PATTERN.matcher(parts[2]).matches()) {
            System.out.println("Veuillez spécifier un niveau et un index valides.");
            return null;
        }
        if (!Level.isValidLevel(Integer.parseInt(parts[1]))) {
            System.out.println("Niveau invalide, veuillez entrer un niveau de carte valide.");
            return null;
        }
        var level = Level.getLevelFromInt(Integer.parseInt(parts[1]));
        var index = Integer.parseInt(parts[2]);

        return new ReserveCardAction(level, index);
    }

    private Action handleBuyAction(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Veuillez spécifier le niveau et l'index de la carte à acheter.");
            return null;
        }

        if (!NUMBER_PATTERN.matcher(parts[1]).matches() || !NUMBER_PATTERN.matcher(parts[2]).matches()) {
            System.out.println("Veuillez spécifier un niveau et un index valides.");
            return null;
        }
        if (!Level.isValidLevel(Integer.parseInt(parts[1]))) {
            System.out.println("Niveau invalide, veuillez entrer un niveau de carte valide.");
            return null;
        }

        var level = Level.getLevelFromInt(Integer.parseInt(parts[1]));
        var index = Integer.parseInt(parts[2]);

        return new BuyCardAction(level, index);
    }

    private Action handleTakeAction(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Veuillez spécifier la couleur de gemme à prendre.");
            return null;
        }
        var gems = new EnumMap<Gem, Integer>(Gem.class);
        for (int i = 1; i < parts.length; i++) {
            if (!Gem.isValidGem(parts[i])) {
                System.out.println("Couleur de gemme invalide : " + parts[i]);
                return null;
            }
            gems.merge(Gem.getGemFromColor(parts[i]), 1, Integer::sum);
        }

        return new TakeGemsAction(new GemBank(gems));
    }
}
