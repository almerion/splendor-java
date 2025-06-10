package src.view.zenView.game;

import src.model.cards.Card;
import src.view.zenView.UiUtils;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class BoughtCardsView {
    public static void drawBoughtCardsView(Graphics2D g2d, int w, int h, List<Card> boughtCards) {
        Objects.requireNonNull(g2d);
        Objects.requireNonNull(boughtCards);
        if (w <= 0 || h <= 0) {
            throw new IllegalArgumentException("Width and height must be positive");
        }

        var rectWidth = Math.round(w * 0.6f);
        var rectHeight = Math.round(h * 0.3f);
        var x = 0;
        var y = h - rectHeight;

        var rect = new Rectangle(x, y, rectWidth, rectHeight);
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(2f));
        g2d.setColor(Color.WHITE);
        g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
        g2d.setStroke(oldStroke);

        if (boughtCards.isEmpty()) {
            g2d.setColor(Color.LIGHT_GRAY);
            UiUtils.centerTextInRect(g2d, "Aucune carte achetée", rect);
            return;
        }

        var cardWidth = rectWidth / 10;
        var cardHeight = rectHeight / 3;
        var xOffset = rect.x;
        var yOffset = rect.y;


        for (var i = 0; i < boughtCards.size(); i++) {
            var card = boughtCards.get(i);
            Objects.requireNonNull(card);
            if (i > 0 && i % 10 == 0) {
                xOffset = rect.x;
                yOffset += cardHeight;
            }
            Rectangle cardRect = new Rectangle(xOffset, yOffset, cardWidth, cardHeight);
            UiUtils.drawCard(g2d, cardRect, card);
            xOffset += cardWidth;
        }
        g2d.setStroke(oldStroke);
    }
}
