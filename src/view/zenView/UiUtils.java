package src.view.zenView;

import src.model.Board;
import src.model.GemBank;
import src.model.cards.Card;
import src.model.cards.Noble;
import src.model.utils.Gem;

import java.awt.*;
import java.util.*;
import java.util.List;

public class UiUtils {
    public static List<MenuButton> createCenteredButtons(int screenW, int screenH, String[] labels) {
        var btnW = screenW / 2;
        var btnH = screenH / 13;
        var spacing = btnH / 3;
        var startY = screenH / labels.length;

        List<MenuButton> list = new ArrayList<>();
        for (int i = 0; i < labels.length; i++) {
            var x = (screenW - btnW) / 2;
            var y = startY + i * (btnH + spacing);
            Rectangle rect = new Rectangle(x, y, btnW, btnH);
            list.add(new MenuButton(labels[i], rect));
        }
        return list;
    }

    public static void drawGem(Graphics2D g2d, Rectangle rect, Gem gem, int value) {
        Objects.requireNonNull(g2d);
        Objects.requireNonNull(gem);
        if (!isValidRectangle(rect)) {
            throw new IllegalArgumentException("Invalid rectangle dimensions");
        }
        if (value < 0) {
            throw new IllegalArgumentException("Value cannot be negative");
        }

        var oldColor = g2d.getColor();
        g2d.setColor(Color.GRAY);
        g2d.fillOval(rect.x, rect.y, rect.width, rect.height);
        g2d.setColor(gem.color());
        g2d.fillOval(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2);
        g2d.setColor(Color.GRAY);
        centerTextInRect(g2d, String.valueOf(value), rect);
        g2d.setColor(oldColor);
    }

    public static void drawCard(Graphics2D g2d, Rectangle rect, Card card) {
        Objects.requireNonNull(g2d);
        Objects.requireNonNull(card);
        if (!isValidRectangle(rect)) {
            throw new IllegalArgumentException("Invalid rectangle dimensions");
        }

        g2d.setColor(card.bonus().color());
        g2d.fillRect(rect.x, rect.y, rect.width, rect.height);

        g2d.setColor(Color.BLACK);
        g2d.drawRect(rect.x, rect.y, rect.width, rect.height);

        // draw number of prestige
        g2d.setColor(Color.GRAY);
        FontMetrics fm = g2d.getFontMetrics();
        String prestige = String.valueOf(card.prestigePoints());
        int textX = rect.x + (rect.width - fm.stringWidth(prestige));
        int textY = rect.y + fm.getAscent();
        g2d.drawString(prestige, textX, textY);


        var priceEntries = card.price().getBank().entrySet();
        var gemWidth = rect.width / (Gem.values().length - 1);
        var gemRect = new Rectangle(
                rect.x + rect.width - gemWidth,
                rect.y + fm.getHeight(),
                gemWidth,
                rect.height - fm.getHeight()
        );
        drawGemsVerticallyHidingZero(g2d, gemRect, priceEntries);
    }

    public static void drawNoble(Graphics2D g2d, Rectangle rect, Noble noble) {
        Objects.requireNonNull(g2d);
        Objects.requireNonNull(noble);
        if (!isValidRectangle(rect)) {
            throw new IllegalArgumentException("Invalid rectangle dimensions");
        }

        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(rect.x, rect.y, rect.width, rect.height);

        g2d.setColor(Color.BLACK);
        g2d.drawRect(rect.x, rect.y, rect.width, rect.height);

        var priceEntries = noble.requiredBonuses().getBank().entrySet();
        drawGemsVerticallyHidingZero(g2d, rect, priceEntries);

        centerTextInRect(g2d, noble.name(), rect);
    }

    private static void drawGemsVerticallyHidingZero(Graphics2D g2d, Rectangle rect, Set<Map.Entry<Gem, Integer>> priceEntries) {
        var maxNbOfGemsDisplayed = Gem.values().length - 1; // Exclude yellow gem
        int gemSize = rect.height / maxNbOfGemsDisplayed;
        int xOffset = rect.x;
        int yOffset = rect.y;
        var diffToMakeSquare = rect.width - gemSize;
        var filteredEntries = priceEntries.stream()
                .filter(e -> e.getValue() > 0)
                .toList();

        for (int i = 0; i < filteredEntries.size(); i++) {
            var entry = filteredEntries.get(i);
            Gem gem = entry.getKey();
            int value = entry.getValue();
            Rectangle gemRect = new Rectangle(xOffset + diffToMakeSquare, yOffset, gemSize, gemSize);
            drawGem(g2d, gemRect, gem, value);
            yOffset += gemSize;
        }
    }

    public static void drawGemsHorizontally(Graphics2D g2d, Rectangle container, GemBank bank) {
        Objects.requireNonNull(g2d);
        Objects.requireNonNull(bank);
        Objects.requireNonNull(container);
        if (!UiUtils.isValidRectangle(container)) {
            throw new IllegalArgumentException("Invalid rectangle dimensions");
        }


        var gemSize = container.height;
        var xOffset = container.x;
        var yOffset = container.y;
        // spacing evenly distributed between and around the gems
        var spacing = (container.width - (gemSize * Gem.values().length)) / (Gem.values().length + 1);
        xOffset += spacing;

        for (var gem : Gem.values()) {
            Rectangle gemRect = new Rectangle(xOffset, yOffset, gemSize, gemSize);
            UiUtils.drawGem(g2d, gemRect, gem, bank.get(gem));
            xOffset += gemSize + spacing;
        }
    }

    public static void drawMenu(Graphics2D g2d, int w, int h, List<MenuButton> buttons, String subtitle) {
        Objects.requireNonNull(g2d);
        Objects.requireNonNull(buttons);
        if (buttons.isEmpty()) {
            throw new IllegalArgumentException("Buttons list cannot be empty");
        }
        Objects.requireNonNull(subtitle);
        if (subtitle.isBlank()) {
            throw new IllegalArgumentException("Subtitle cannot be blank");
        }
        if (w <= 0 || h <= 0) {
            throw new IllegalArgumentException("Width and height must be positive");
        }

        var subY = (h / buttons.size()) - (buttons.getFirst().rect().height / 2);
        g2d.setColor(new Color(255, 255, 255, 180));
        centerText(g2d, subtitle, w, subY);

        for (var btn : buttons) {
            drawButton(g2d, btn);
        }
    }

    public static void clearScreen(Graphics2D g2d, int w, int h) {
        Objects.requireNonNull(g2d);
        if (w <= 0 || h <= 0) {
            throw new IllegalArgumentException("Width and height must be positive");
        }
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, w, h);
    }

    public static void drawButton(Graphics2D g2d, MenuButton btn) {
        Objects.requireNonNull(g2d);
        Objects.requireNonNull(btn);

        var r = btn.rect();
        if (!isValidRectangle(r)) {
            throw new IllegalArgumentException("Invalid rectangle dimensions");
        }

        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(r.x, r.y, r.width, r.height);

        Stroke old = g2d.getStroke();
        g2d.setStroke(new BasicStroke(3f));
        g2d.setColor(Color.WHITE);
        g2d.drawRect(r.x, r.y, r.width, r.height);
        g2d.setStroke(old);

        g2d.setColor(Color.WHITE);
        centerTextInRect(g2d, btn.label(), r);
    }

    public static void centerText(Graphics2D g2d, String text, int totalWidth, int y) {
        Objects.requireNonNull(g2d);
        Objects.requireNonNull(text);
        if (text.isBlank()) {
            throw new IllegalArgumentException("Text cannot be blank");
        }
        if (totalWidth <= 0) {
            throw new IllegalArgumentException("Total width must be positive");
        }

        FontMetrics fm = g2d.getFontMetrics();
        int x = (totalWidth - fm.stringWidth(text)) / 2;
        g2d.drawString(text, x, y);
    }

    public static void centerTextInRect(Graphics2D g2d, String text, Rectangle r) {
        Objects.requireNonNull(g2d);
        Objects.requireNonNull(text);
        if (!isValidRectangle(r)) {
            throw new IllegalArgumentException("Invalid rectangle dimensions");
        }
        if (text.isBlank()) {
            throw new IllegalArgumentException("Text cannot be blank");
        }

        FontMetrics fm = g2d.getFontMetrics();
        int textX = r.x + (r.width - fm.stringWidth(text)) / 2;
        int textY = r.y + ((r.height - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(text, textX, textY);
    }

    public static boolean isValidRectangle(Rectangle rect) {
        Objects.requireNonNull(rect);
        return rect.width > 0 && rect.height > 0 && rect.x >= 0 && rect.y >= 0;
    }
}
