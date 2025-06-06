package src.view.zenView.game;

import src.model.Game;
import src.model.Player;
import src.view.zenView.UiUtils;

import java.awt.*;
import java.util.Objects;

public class PlayersView {
    public static void drawPlayersView(Graphics2D g2d, int w, int h, Game game) {
        Objects.requireNonNull(g2d);
        Objects.requireNonNull(game);
        if (w <= 0 || h <= 0) {
            throw new IllegalArgumentException("Width and height must be positive");
        }

        var rectWidth = Math.round(w * 0.2f);
        var rectHeight = Math.round(h * 0.6f);
        var x = w - rectWidth;
        var y = 0;

        var container = new Rectangle(x, y, rectWidth, rectHeight);
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(2f));
        g2d.setColor(Color.WHITE);
        g2d.drawRect(container.x, container.y, container.width, container.height);
        g2d.setStroke(oldStroke);

        for (int i = 0; i < game.players().size(); i++) {
            var player = game.players().get(i);
            Objects.requireNonNull(player);
            var playerRect = new Rectangle(container.x, container.y + (i * rectHeight / 4), container.width, rectHeight / 4);

            boolean isCurrentPlayer = game.currentPlayerIndex() == i;
            drawPlayer(g2d, playerRect, player, i, isCurrentPlayer);
        }
    }

    private static void drawPlayer(Graphics2D g2d, Rectangle rect, Player player, int playerIndex, boolean isCurrentPlayer) {
        Objects.requireNonNull(g2d);
        Objects.requireNonNull(rect);
        Objects.requireNonNull(player);
        if (!UiUtils.isValidRectangle(rect)) {
            throw new IllegalArgumentException("Invalid rectangle dimensions");
        }
        if (playerIndex < 0) {
            throw new IllegalArgumentException("Player index must be non-negative");
        }

        var rectHeight = rect.height / 3; // 3 rows
        var halfWidth = rect.width / 2;
        var playerName = "Player " + (playerIndex + 1);
        var score = "Prestige: " + player.getPoints();
        var nbCards = "nbCartes: " + player.cards().size();
        var nbReservedCards = "nbRéservées: " + player.reservedCards().size();

        var oldColor = g2d.getColor();
        g2d.setColor(isCurrentPlayer ? Color.YELLOW : Color.LIGHT_GRAY);

        var playerNameRect = new Rectangle(rect.x, rect.y, halfWidth, rectHeight);
        UiUtils.centerTextInRect(g2d, playerName, playerNameRect);
        var scoreRect = new Rectangle(rect.x + halfWidth, rect.y, halfWidth, rectHeight);
        UiUtils.centerTextInRect(g2d, score, scoreRect);
        var nbCardsRect = new Rectangle(rect.x, rect.y + rectHeight, halfWidth, rectHeight);
        UiUtils.centerTextInRect(g2d, nbCards, nbCardsRect);
        var nbReservedCardsRect = new Rectangle(rect.x + halfWidth, rect.y + rectHeight, halfWidth, rectHeight);
        UiUtils.centerTextInRect(g2d, nbReservedCards, nbReservedCardsRect);
        var gemsRect = new Rectangle(rect.x, rect.y + 2 * rectHeight, rect.width, rectHeight);
        g2d.setColor(Color.WHITE);
        UiUtils.drawGemsHorizontally(g2d, gemsRect, player.gems());
        g2d.drawLine(rect.x, rect.y + rect.height - 1, rect.x + rect.width, rect.y + rect.height - 1);
        g2d.setColor(oldColor);
    }
}
