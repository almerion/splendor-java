package src.view.zenView;

import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.PointerEvent;

import java.util.List;
import java.util.Objects;

public class PlayerCountView {
    private final ApplicationContext context;

    public PlayerCountView(ApplicationContext context) {
        Objects.requireNonNull(context);
        this.context = context;
    }

    public int getNumberOfPlayers() {
        var screen = context.getScreenInfo();
        int w = screen.width();
        int h = screen.height();


        List<MenuButton> buttons = UiUtils.createCenteredButtons(w, h, new String[] { "2 joueurs", "3 joueurs", "4 joueurs" });

        context.renderFrame(g2d -> {
            UiUtils.clearScreen(g2d, w, h);
            UiUtils.drawMenu(g2d, w, h, buttons, "Choisissez le nombre de joueurs");
        });

        while (true) {
            PointerEvent evt = ZenView.waitForUserClick(context, Long.MAX_VALUE);
            if (evt == null) {
                continue;
            }

            for (var btn : buttons) {
                if (btn.rect().contains(evt.location().x(), evt.location().y())) {
                    switch (btn.label()) {
                        case "2 joueurs": return  2;
                        case "3 joueurs": return  3;
                        case "4 joueurs": return  4;
                        default: {
                            // maybe show error message ? idk
                        }
                    };
                }
            }
        }
    }

}
