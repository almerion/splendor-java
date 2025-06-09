package src.view.zenView.actions;

import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import src.model.GemBank;
import src.model.utils.Gem;
import src.utils.DropTokensAction;
import src.view.zenView.ZenView;

import java.awt.*;
import java.util.EnumMap;
import java.util.Objects;

public class DropActionView {
    private final ApplicationContext context;
    private final EnumMap<Gem, Integer> dropTokens = new EnumMap<>(Gem.class);

    public DropActionView(ApplicationContext context) {
        Objects.requireNonNull(context);
        this.context = context;
    }

    public void drawDropAction() {
        Objects.requireNonNull(context);
        ActionsView.clearActionsView(context);
        var container = ActionsView.getActionsViewContainer(context);

        context.renderFrame(g2d -> {
            var currentY = container.y + 20;
            var oldColor = g2d.getColor();
            g2d.setColor(Color.WHITE);
            g2d.drawString("Selectionnez les gemmes à rendre puis apuillez sur \"m\" pour valider ", container.x + 10, currentY);
            var gems = Gem.values();
            for (var i = 0; i < gems.length; i++) {
                var gem = gems[i];
                if (gem == Gem.YELLOW) continue;
                g2d.drawString((char) ('a' + i) + ". " + gem.name(), container.x + 10, currentY += 20);
            }
            g2d.setColor(oldColor);
        });
    }

    public DropTokensAction readDropTokensAction() {
        Objects.requireNonNull(context);
        var container = ActionsView.getActionsViewContainer(context);
        var newY = container.y + 40 + (Gem.values().length - 1) * 20 ;
        var mapState = new Rectangle(container.x, newY, container.width, container.height - newY + container.y);

        while (true) {
            drawCurrentMapState(mapState);
            KeyboardEvent keyboardEvent;
            do {
                keyboardEvent = ZenView.waitForUserKeyPress(context, Long.MAX_VALUE);
            } while (keyboardEvent == null || (keyboardEvent.key() != KeyboardEvent.Key.M && (keyboardEvent.key().ordinal() < 11 || keyboardEvent.key().ordinal() >= 11 + Gem.values().length -1)));

            if (keyboardEvent.key() == KeyboardEvent.Key.M) {
                return new DropTokensAction(new GemBank(dropTokens));
            }

            var gem = Gem.values()[keyboardEvent.key().ordinal() - 11];
            dropTokens.merge(gem, 1, Integer::sum);
        }
    }

    public void drawCurrentMapState(Rectangle mapState) {
        context.renderFrame(g2d -> {
            var oldColor = g2d.getColor();

            g2d.setColor(Color.BLACK);
            g2d.clearRect(mapState.x, mapState.y, mapState.width, mapState.height);

            g2d.setColor(Color.WHITE);
            g2d.drawString("Current Drop Tokens:", mapState.x + 10, mapState.y + 20);
            int currentY = mapState.y + 40;
            for (var entry : dropTokens.entrySet()) {
                var gem = entry.getKey();
                var count = entry.getValue();
                g2d.drawString(gem.name() + ": " + count, mapState.x + 10, currentY);
                currentY += 20;
            }
            g2d.setColor(oldColor);
        });
    }
}
