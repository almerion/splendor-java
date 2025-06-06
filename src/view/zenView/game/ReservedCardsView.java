package src.view.zenView.game;

import src.model.cards.Card;
import src.view.zenView.UiUtils;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class ReservedCardsView {
    // draws the reserved cards in a rectangle at the bottom right of the screen with width = 20% of the screen width and height = 20% of the screen height
    public static void drawReservedCardsView(Graphics2D g2d, int width, int height, List<Card> cards) {
        Objects.requireNonNull(g2d);
        Objects.requireNonNull(cards);
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive");
        }

        var rectWidth = Math.round(width * 0.2f);
        var rectHeight = Math.round(height * 0.2f);
        var x = width  - rectWidth;
        var y = height - rectHeight;
        var container = new Rectangle(x, y, rectWidth, rectHeight);

        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(2f));
        g2d.setColor(Color.WHITE);
        g2d.drawRect(container.x, container.y, container.width, container.height);
        g2d.setStroke(oldStroke);

        if (cards.isEmpty()) {
            g2d.setColor(Color.LIGHT_GRAY);
            String msg = "Aucune carte réservée";
            UiUtils.centerTextInRect(g2d, msg, container);
            return;
        }

        var cardWidth = rectWidth / cards.size();
        var cardHeight = rectHeight;
        var xOffset = container.x;
        var yOffset = container.y;
        for (Card card : cards) {
            if (card == null) {
                throw new IllegalStateException();
            }
            Rectangle cardRect = new Rectangle(xOffset, yOffset, cardWidth, cardHeight);
            UiUtils.drawCard(g2d, cardRect, card);
            xOffset += cardWidth;
        }
    }
}
