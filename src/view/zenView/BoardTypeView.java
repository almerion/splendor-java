package src.view.zenView;

import com.github.forax.zen.ApplicationContext;
import src.model.utils.BoardType;

import java.awt.*;
import java.util.Objects;

public class BoardTypeView {
    private final ApplicationContext context;

    public BoardTypeView(ApplicationContext context) {
        Objects.requireNonNull(context);
        this.context = context;
    }


    public BoardType getBoardType() {
        var screen = context.getScreenInfo();
        int w = screen.width();
        int h = screen.height();

        var buttons = UiUtils.createCenteredButtons(w, h, new String[] { "Simple", "Complet" });

        context.renderFrame(g2d -> {
            UiUtils.clearScreen(g2d, w, h);
            UiUtils.drawMenu(g2d, w, h, buttons, "Choisissez le type de plateau");
        });

        while (true) {
            var evt = ZenView.waitForUserClick(context, Long.MAX_VALUE);
            if (evt == null) {
                continue;
            }

            for (var btn : buttons) {
                if (btn.rect().contains(evt.location().x(), evt.location().y())) {
                    switch (btn.label()) {
                        case "Simple": return BoardType.SIMPLE;
                        case "Complet": return BoardType.COMPLET;
                        default: {
                            // maybe show error message ? idk
                        }
                    };
                }
            }
        }
    }
}
