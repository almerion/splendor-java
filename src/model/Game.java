package src.model;

import src.model.utils.BoardType;
import src.model.utils.Gem;
import src.utils.*;

import java.util.*;

public class Game {
    private final Board board;
    private final List<Player> players;
    private int currentPlayerIndex;
    private int finalRoundPlayerIndex;
    private boolean gameOver;

    public Game(Board board, List<Player> players, int currentPlayerIndex, int finalRoundPlayerIndex, boolean gameOver) {
        Objects.requireNonNull(board);
        Objects.requireNonNull(players);
        if (currentPlayerIndex < 0 || currentPlayerIndex >= players.size()) {
            throw new IllegalArgumentException("Index du joueur courant invalide");
        }
        if (finalRoundPlayerIndex < -1 || finalRoundPlayerIndex >= players.size()) {
            throw new IllegalArgumentException("Index du joueur de la dernière manche invalide");
        }
        this.board = board;
        this.players = new ArrayList<>(players);
        this.currentPlayerIndex = currentPlayerIndex;
        this.finalRoundPlayerIndex = finalRoundPlayerIndex;
        this.gameOver = gameOver;
    }

    public Board board() {
        return board;
    }

    public List<Player> players() {
        return List.copyOf(players);
    }

    public void nextTurn() {
        if (gameOver) return;

        if (currentPlayerHasTooManyGems()) {
            throw new IllegalStateException("Le joueur courant a trop de gemmes.");
        }

        board.nobles().forEach(noble -> {
            if (players.get(currentPlayerIndex).canClaimNoble(noble)) {
                players.get(currentPlayerIndex).claimNoble(noble);
            }
        });

        // Quand un joueur atteint 15 points, on laisse le tour de table se finir.
        if (players.get(currentPlayerIndex).getPoints() >= 15 && finalRoundPlayerIndex == -1) {
            finalRoundPlayerIndex = currentPlayerIndex;
        }

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

        if (currentPlayerIndex == finalRoundPlayerIndex) {
            gameOver = true;
        }
    }

    public int getWinnerIndex() {
        if (!gameOver) return -1;
        var winner = players.stream()
                .filter(player -> player.getPoints() >= 15)
                .min(Comparator.comparingInt(player -> player.cards().size()))
                .orElse(null);

        return winner != null ? players.indexOf(winner) : -1;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public int currentPlayerIndex() {
        return currentPlayerIndex;
    }

    public boolean isValidAction(Action action) {
        Objects.requireNonNull(action);
        return switch (action) {
            case BuyCardAction buy -> isValidBuyCardAction(buy);
            case BuyReservedAction buyReserved -> isValidBuyReservedAction(buyReserved);
            case TakeGemsAction takeGems -> isValidTakeGemsAction(takeGems);
            case ReserveCardAction reserveCard -> board.boardType() == BoardType.COMPLET && isValidReserveCardAction(reserveCard);
            case DropTokensAction dropTokens -> isValidDropTokensAction(dropTokens);
        };
    }

    public void handleAction(Action action) {
        Objects.requireNonNull(action);
        if (!isValidAction(action)) {
            throw new IllegalArgumentException("Action invalide : " + action);
        }

        switch (action) {
            case BuyCardAction buy -> buyCard(buy);
            case BuyReservedAction buyReserved -> buyReservedCard(buyReserved);
            case TakeGemsAction take -> takeGems(take);
            case ReserveCardAction reserve -> reserveCard(reserve);
            case DropTokensAction drop -> dropTokens(drop);
        }
    }

    private void dropTokens(DropTokensAction drop) {
        Objects.requireNonNull(drop);
        if (!isValidDropTokensAction(drop)) {
            throw new IllegalArgumentException("Action de dépôt de jetons invalide : " + drop);
        }
        var currentPlayer = getCurrentPlayer();
        var gemsToDrop = drop.gemBank();
        if (!currentPlayer.gems().canSubtractBy(gemsToDrop, false)) {
            throw new IllegalArgumentException("Le joueur ne peut pas déposer les gemmes : " + drop);
        }
        currentPlayer.removeGems(gemsToDrop);
        board.addGems(gemsToDrop);
    }

    private void reserveCard(ReserveCardAction reserve) {
        Objects.requireNonNull(reserve);
        if (!isValidReserveCardAction(reserve)) {
            throw new IllegalArgumentException("Action de réservation de carte invalide : " + reserve);
        }
        var currentPlayer = getCurrentPlayer();
        var card = board.getCard(reserve.cardLevel(), reserve.cardIndex());
        if (card == null) {
            throw new IllegalArgumentException("La carte demandée n'existe pas : " + reserve);
        }
        if (!currentPlayer.canReserveCard(card)) {
            throw new IllegalArgumentException("Le joueur ne peut pas réserver la carte : " + card);
        }
        currentPlayer.reserveCard(card);
        board.removeCard(reserve.cardLevel(), reserve.cardIndex());
        var yellowGem = new GemBank(Map.of(Gem.YELLOW, 1));
        if (board.gemBank().canSubtractBy(yellowGem, false)) {
            currentPlayer.addGems(yellowGem);
            board.removeGems(yellowGem);
        }
    }

    private void takeGems(TakeGemsAction take) {
        Objects.requireNonNull(take);
        if (!isValidTakeGemsAction(take)) {
            throw new IllegalArgumentException("Action de prise de gemmes invalide : " + take);
        }
        var currentPlayer = getCurrentPlayer();
        var gemsToTake = take.gemBank();
        if (!board.gemBank().canSubtractBy(take.gemBank(), false)) {
            throw new IllegalArgumentException("La banque de gemmes ne peut pas fournir les gemmes demandées : " + take);
        }
        currentPlayer.addGems(gemsToTake);
        board.removeGems(gemsToTake);
    }

    private void buyReservedCard(BuyReservedAction buyReserved) {
        Objects.requireNonNull(buyReserved);
        if (!isValidBuyReservedAction(buyReserved)) {
            throw new IllegalArgumentException("Action d'achat de carte réservée invalide : " + buyReserved);
        }
        var currentPlayer = getCurrentPlayer();
        var cardIndex = buyReserved.reservedCardIndex();
        if (cardIndex < 0 || cardIndex >= currentPlayer.reservedCards().size()) {
            throw new IllegalArgumentException("Index de carte réservée invalide : " + cardIndex);
        }
        var reservedCard = currentPlayer.reservedCards().get(cardIndex);
        if (!currentPlayer.canPurchaseCard(reservedCard)) {
            throw new IllegalArgumentException("Le joueur ne peut pas acheter la carte réservée : " + reservedCard);
        }
        var cardPriceWithBonuses = reservedCard.price().subtractOrZero(currentPlayer.getBonuses());
        if (!currentPlayer.gems().canSubtractBy(cardPriceWithBonuses, true)) {
            throw new IllegalArgumentException("Le joueur ne peut pas acheter la carte réservée : " + reservedCard);
        }
        currentPlayer.removeGems(cardPriceWithBonuses);
        board.addGems(cardPriceWithBonuses);
        currentPlayer.addCard(reservedCard);
        currentPlayer.removeReservedCard(cardIndex);
    }

    private boolean isValidDropTokensAction(DropTokensAction dropTokens) {
        Objects.requireNonNull(dropTokens);
        var currentPlayer = getCurrentPlayer();
        var currentNumberOfGems = currentPlayer.gems().getBank().values().stream().mapToInt(Integer::intValue).sum();
        var numberOfGemsToDrop = dropTokens.gemBank().getBank().values().stream().mapToInt(Integer::intValue).sum();
        if (currentNumberOfGems - numberOfGemsToDrop < 0 || currentNumberOfGems - numberOfGemsToDrop > 10) {
            return false;
        }
        return currentPlayer.gems().canSubtractBy(dropTokens.gemBank(), false);
    }

    private boolean isValidReserveCardAction(ReserveCardAction reserveCard) {
        Objects.requireNonNull(reserveCard);
        var currentPlayer = getCurrentPlayer();
        var card = board.getCard(reserveCard.cardLevel(), reserveCard.cardIndex());
        if (card == null) {
            return false;
        }
        return currentPlayer.canReserveCard(card);
    }

    private boolean isValidTakeGemsAction(TakeGemsAction takeGems) {
        Objects.requireNonNull(takeGems);
        var gemsToTake = takeGems.gemBank().getBank();

        if (gemsToTake.getOrDefault(Gem.YELLOW, 0) > 0) {
            return false;
        }

        var totalGems = gemsToTake.values().stream().mapToInt(Integer::intValue).sum();

        if (totalGems == 3) {
            return gemsToTake
                    .entrySet()
                    .stream()
                    .allMatch(e -> {
                        if (e.getValue() > 1) return false;
                        return board.gemBank().get(e.getKey()) >= 1;
                    });
        } else if (totalGems == 2) {
            var currentGem = gemsToTake.entrySet().stream().filter(e -> e.getValue() == 2).findFirst();
            return currentGem.isPresent() && board.gemBank().get(currentGem.get().getKey()) >= 4;
        }

        return false;
    }

    public boolean isValidBuyCardAction(BuyCardAction buyCardAction) {
        Objects.requireNonNull(buyCardAction);
        var currentPlayer = getCurrentPlayer();
        var card = board.getCard(buyCardAction.cardLevel(), buyCardAction.cardIndex());
        if (card == null) {
            return false;
        }
        return currentPlayer.canPurchaseCard(card);
    }

    private void buyCard(BuyCardAction buyCardAction) {
        Objects.requireNonNull(buyCardAction);
        if (!isValidBuyCardAction(buyCardAction)) {
            throw new IllegalArgumentException("Action d'achat de carte invalide : " + buyCardAction);
        }
        var currentPlayer = getCurrentPlayer();
        var card = board.getCard(buyCardAction.cardLevel(), buyCardAction.cardIndex());
        if (card == null) {
            throw new IllegalArgumentException("La carte demandée n'existe pas : " + buyCardAction);
        }
        var bonuses = currentPlayer.getBonuses();
        var cardPriceWithBonuses = card.price().subtractOrZero(bonuses);
        if (!currentPlayer.gems().canSubtractBy(cardPriceWithBonuses, true)) {
            throw new IllegalArgumentException("Le joueur ne peut pas acheter la carte : " + buyCardAction);
        }

        currentPlayer.removeGems(cardPriceWithBonuses);
        board.addGems(cardPriceWithBonuses);
        board.removeCard(buyCardAction.cardLevel(), buyCardAction.cardIndex());
        currentPlayer.addCard(card);
    }

    public boolean isValidBuyReservedAction(BuyReservedAction buyReservedAction) {
        Objects.requireNonNull(buyReservedAction);
        var currentPlayer = getCurrentPlayer();
        var cardIndex = buyReservedAction.reservedCardIndex();
        if (cardIndex < 0 || cardIndex >= currentPlayer.reservedCards().size()) {
            return false;
        }

        var reservedCard = currentPlayer.reservedCards().get(cardIndex);
        return currentPlayer.canPurchaseCard(reservedCard);
    }

    public boolean currentPlayerHasTooManyGems() {
        var currentPlayer = getCurrentPlayer();
        var gems = currentPlayer.gems();
        return gems.getBank().values().stream().mapToInt(Integer::intValue).sum() > 10;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public static Game createGame(int nbPlayers, BoardType boardType) {
        if (!isValidNumberOfPlayers(nbPlayers)) {
            throw new IllegalArgumentException("Nombre de joueurs invalide : " + nbPlayers);
        }
        if (nbPlayers < 1 || nbPlayers > 4) throw new IllegalArgumentException();
        var currentPlayerIndex = 0;
        var gameOver = false;
        var finalRoundPlayerIndex = -1;
        var players = new ArrayList<Player>();
        var board = Board.createBoard(nbPlayers, boardType);

        for (int i = 0; i < nbPlayers; i++) {
            players.add(Player.createEmptyPlayer());
        }

        return new Game(board, players, currentPlayerIndex, finalRoundPlayerIndex, gameOver);
    }

    public static boolean isValidNumberOfPlayers(int nbPlayers) {
        return nbPlayers >= 2 && nbPlayers <= 4;
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
