package src.view.zenView.game;

import src.model.Board;
import src.model.cards.Card;
import src.model.cards.Noble;
import src.model.utils.Gem;
import src.model.utils.Level;
import src.view.zenView.UiUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BoardView {
    public static void drawBoard(Graphics2D g2d, int width, int height, Board board) {
        Objects.requireNonNull(g2d);
        Objects.requireNonNull(board);
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive");
        }

        var rectWidth = Math.round(width * 0.6f);
        var rectHeight = Math.round(height * 0.6f);
        var container = new Rectangle(0, 0, rectWidth, rectHeight);

        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(2f));
        g2d.setColor(Color.WHITE);
        g2d.drawRect(container.x, container.y, container.width, container.height);
        g2d.setStroke(oldStroke);

        drawNobles(g2d, container, board);
        drawCards(g2d, container, board);
        var gemsRect = new Rectangle(container.x, container.y + (container.height / 5) * 4, container.width, container.height / 5);
        UiUtils.drawGemsHorizontally(g2d, gemsRect, board.gemBank());
    }

    private static void drawCards(Graphics2D g2d, Rectangle container, Board board) {
        Objects.requireNonNull(g2d);
        Objects.requireNonNull(board);
        Objects.requireNonNull(container);
        if (!UiUtils.isValidRectangle(container)) {
            throw new IllegalArgumentException("Invalid rectangle dimensions");
        }

        var cardRows = board.cardRows();
        var xOffset = container.x;
        var yOffset = container.y + container.height / 5;
        var cardWidth = container.width / board.cardsShown();
        var cardHeight = (container.height) / 5;


        for (Level level : Level.values()) {
            List<Card> cards = cardRows.get(level);
            if (cards == null || cards.isEmpty()) continue;

            for (Card card : cards) {
                Rectangle cardRect = new Rectangle(xOffset, yOffset, cardWidth, cardHeight);
                UiUtils.drawCard(g2d, cardRect, card);
                xOffset += cardWidth;
            }

            xOffset = container.x;
            yOffset += cardHeight;
        }
    }

    private static void drawNobles(Graphics2D g2d, Rectangle rect, Board board) {
        Objects.requireNonNull(g2d);
        Objects.requireNonNull(board);
        Objects.requireNonNull(rect);
        if (!UiUtils.isValidRectangle(rect)) {
            throw new IllegalArgumentException("Invalid rectangle dimensions");
        }

        var nobles = board.nobles();
        var xOffset = rect.x;
        var yOffset = rect.y;
        var cardWidth = rect.width / board.noblesShown();
        var cardHeight = rect.height / 5;

        for (Noble noble : nobles) {
            Rectangle nobleRect = new Rectangle(xOffset, yOffset, cardWidth, cardHeight);
            UiUtils.drawNoble(g2d, nobleRect, noble);
            xOffset += cardWidth;
        }
    }
}
