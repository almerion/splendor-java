package src.view.zenView.actions;

import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import src.model.Game;
import src.model.GemBank;
import src.model.utils.BoardType;
import src.model.utils.Gem;
import src.model.utils.Level;
import src.utils.*;
import src.view.zenView.UiUtils;
import src.view.zenView.ZenView;

import java.awt.*;
import java.util.HashMap;
import java.util.Objects;

public class ActionsView {
    private final ApplicationContext context;
    private final Game game;
    private final Rectangle container;
    private final static String[] ACTIONS = {
            "a. Acheter une carte",
            "b. Prendre 3 gemmes de couleurs différentes",
            "c. Prendre 2 gemmes de la même couleur",
            "d. Reserver une carte",
            "e. Acheter une carte réservée",
    };

    public ActionsView(ApplicationContext context, Game game, Rectangle container) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(game);
        Objects.requireNonNull(container);
        if (!UiUtils.isValidRectangle(container)) {
            throw new IllegalArgumentException("Invalid rectangle dimensions");
        }
        this.game = game;
        this.context = context;
        this.container = container;
    }

    public Action getUserAction() {
        KeyboardEvent keyboardEvent = ZenView.waitForUserKeyPress(context, Long.MAX_VALUE);
        if (keyboardEvent == null) {
            return Action.invalidAction();
        }
        var key = keyboardEvent.key();

        return switch (key) {
            case A -> handleBuy();
            case B -> handleBuyThreeDifferentGems();
            case C -> handleTakeTwoGems();
            case D -> game.board().boardType() == BoardType.SIMPLE ? Action.invalidAction() : handleReserve();
            case E -> game.board().boardType() == BoardType.SIMPLE || game.getCurrentPlayer().reservedCards().isEmpty() ? Action.invalidAction() : handleBuyReserved();
            default -> Action.invalidAction();
        };
    }

    private Action handleBuyReserved() {
        clearActionsView(context);
        context.renderFrame(g2d -> {
            var currentY = container.y + 20;
            var oldColor = g2d.getColor();
            g2d.setColor(Color.WHITE);
            g2d.drawString("Acheter une carte réservée", container.x + 10, currentY);

            var reservedCards = game.getCurrentPlayer().reservedCards();
            for (var i = 0; i < reservedCards.size(); i ++) {
                g2d.drawString((char) ('a' + i) + ". carte reservée n°" + i, container.x + 10, currentY += 20);
            }
            g2d.setColor(oldColor);
        });

        KeyboardEvent keyboardEvent;
        do {
            keyboardEvent = ZenView.waitForUserKeyPress(context, Long.MAX_VALUE);
        } while (keyboardEvent == null || keyboardEvent.key().ordinal() < 11 || keyboardEvent.key().ordinal() > 10 + game.getCurrentPlayer().reservedCards().size());
        var cardIndex = keyboardEvent.key().ordinal() - 11;
        return new BuyReservedAction(cardIndex);
    }

    private Action handleTakeTwoGems() {
        clearActionsView(context);
        context.renderFrame(g2d -> {
            var currentY = container.y + 10;
            var oldColor = g2d.getColor();
            g2d.setColor(Color.WHITE);
            g2d.drawString("Prendre 2 gemmes de la même couleur", container.x + 10, currentY);

            var gems = Gem.values();
            for(var i = 0; i < gems.length; i++) {
                var gem = gems[i];
                if (gem == Gem.YELLOW) continue;
                g2d.drawString((char) ('a' + i) + ". " + gem.name(), container.x + 10, currentY += 20);
            }
            g2d.setColor(oldColor);
        });

        var gems = Gem.values();
        var bank = new HashMap<Gem, Integer>();
        while (bank.isEmpty()) {
            KeyboardEvent keyboardEvent = ZenView.waitForUserKeyPress(context, Long.MAX_VALUE);
            if (keyboardEvent == null) {
                continue;
            }
            var key = keyboardEvent.key();
            if (key.ordinal() >= 11 && key.ordinal() < 11 + gems.length - 1) {
                var gem = gems[key.ordinal() - 11];
                bank.put(gem, 2);
            }
        }
        clearActionsView(context);

        return new TakeGemsAction(new GemBank(bank));
    }

    private Action handleBuyThreeDifferentGems() {
        clearActionsView(context);
        context.renderFrame(g2d -> {
            var currentY = container.y + 10;
            var oldColor = g2d.getColor();
            g2d.setColor(Color.WHITE);
            g2d.drawString("Prendre 3 gemmes de couleurs différentes", container.x + 10, currentY);

            var gems = Gem.values();
            for (var i = 0; i < gems.length; i++) {
                var gem = gems[i];
                if (gem == Gem.YELLOW) continue;
                g2d.drawString((char) ('a' + i) + ". " + gem.name(), container.x + 10, currentY += 20);
            }
            g2d.setColor(oldColor);
        });

        var gems = Gem.values();
        var bank = new HashMap<Gem, Integer>();
        while (bank.size() < 3) {
            KeyboardEvent keyboardEvent = ZenView.waitForUserKeyPress(context, Long.MAX_VALUE);
            if (keyboardEvent == null) {
                continue;
            }
            var key = keyboardEvent.key();

            if (key.ordinal() >= 11 && key.ordinal() < 11 + gems.length) {
                var gem = gems[key.ordinal() - 11];
                if (!bank.containsKey(gem)) {
                    bank.put(gem, 1);
                    context.renderFrame(g2d -> {
                        var currentY = container.y + 10 + ((Gem.values().length - 1) * 20) + 20 * bank.size();
                        var oldColor = g2d.getColor();
                        g2d.setColor(Color.WHITE);
                        g2d.drawString("Selected: " + gem.name(), container.x + 10, currentY);
                        g2d.setColor(oldColor);
                    });
                }
            }
        }
        clearActionsView(context);
        return new TakeGemsAction(new GemBank(bank));
    }

    private Action handleReserve() {
        var level = getLevel();
        var cardIndex = getCardIndex();
        return new ReserveCardAction(level, cardIndex);
    }

    private Action handleBuy() {
        var level = getLevel();
        var cardIndex = getCardIndex();
        return new BuyCardAction(level, cardIndex);
    }

    private Level getLevel() {
        clearActionsView(context);
        var rect = new Rectangle(container.x, container.y, container.width, container.height / 2);
        context.renderFrame(g2d -> {
            var oldColor = g2d.getColor();
            g2d.setColor(Color.WHITE);
            g2d.drawString("Niveau de la carte :", rect.x + 10, rect.y + 20);
            g2d.drawString("a : 1", rect.x + 10, rect.y + 40);
            g2d.drawString("b : 2", rect.x + 10, rect.y + 60);
            g2d.drawString("c : 3", rect.x + 10, rect.y + 80);
            g2d.setColor(oldColor);
        });

        KeyboardEvent keyboardEvent;
        do {
            keyboardEvent = ZenView.waitForUserKeyPress(context, Long.MAX_VALUE);
        }while (keyboardEvent == null || keyboardEvent.key().ordinal() < 11 || keyboardEvent.key().ordinal() > 13);

        var level = switch (keyboardEvent.key()) {
            case A -> Level.ONE;
            case B -> Level.TWO;
            case C -> Level.THREE;
            default -> throw new IllegalStateException("Invalid kye for level selection: " + keyboardEvent.key());
        };
        clearActionsView(context);
        return level;
    }

    private int getCardIndex() {
        clearActionsView(context);
        KeyboardEvent keyboardEvent;
        context.renderFrame(g2d -> {
            var oldColor = g2d.getColor();
            g2d.setColor(Color.WHITE);
            g2d.drawString("Numero de la carte :", container.x + 10, container.y + 20);
            for (var i = 0; i < game.board().cardsShown(); i++) {
                g2d.drawString((char) ('a' + i) + ". " + i, container.x + 10, container.y + 40 + (i * 20));
            }
            g2d.setColor(oldColor);
        });

        do {
            keyboardEvent = ZenView.waitForUserKeyPress(context, Long.MAX_VALUE);
        } while (keyboardEvent == null || keyboardEvent.key().ordinal() < 10 || keyboardEvent.key().ordinal() > 10 + game.board().cardsShown());

        var cardIndex = keyboardEvent.key().ordinal() - 11; // 'a' corresponds to 10
        if (cardIndex >= game.board().cardsShown()) {
            throw new IllegalArgumentException("Invalid card index: " + cardIndex);
        }
        clearActionsView(context);
        return cardIndex;
    }

    public void drawActionsView() {
        context.renderFrame(g2d -> {
            Color oldColor = g2d.getColor();
            g2d.setColor(Color.WHITE);

            var to = ACTIONS.length  + (game.board().boardType() == BoardType.SIMPLE ? -2 : 0); // so we don't show reserve
            for (var i = 0; i < to; i++) {
                var action = ACTIONS[i];
                g2d.drawString(action, container.x + 10, container.y + 10 + (20 * i));
            }

            g2d.setColor(oldColor);
        });
    }

    public static void clearActionsView(ApplicationContext context) {
        context.renderFrame(g2d -> {
            var oldColor = g2d.getColor();
            g2d.setColor(Color.BLACK);
            var container = getActionsViewContainer(context);
            g2d.clearRect(container.x, container.y, container.width, container.height);
            g2d.setColor(oldColor);
        });
    }

    public static ActionsView create(ApplicationContext context, Game game) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(game);

        var container = getActionsViewContainer(context);
        return new ActionsView(context, game, container);
    }

    public static Rectangle getActionsViewContainer(ApplicationContext context) {
        Objects.requireNonNull(context);
        var screen = context.getScreenInfo();
        int w = screen.width();
        int h = screen.height();

        var rectWidth = Math.round(w * 0.2f);
        var rectHeight = Math.round(h * 0.4f);
        var x = Math.round(w * 0.6f);
        var y = Math.round(h * 0.6f);

        return new Rectangle(x, y, rectWidth, rectHeight);
    }
}