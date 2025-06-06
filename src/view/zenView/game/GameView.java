package src.view.zenView.game;

import com.github.forax.zen.ApplicationContext;
import src.model.Game;
import src.view.zenView.UiUtils;

import java.util.Objects;

public class GameView {
    private final ApplicationContext context;
    public GameView(ApplicationContext context) {
        Objects.requireNonNull(context);
        this.context = context;
    }

    public void displayGameState(Game game) {
        Objects.requireNonNull(game);
        var screen = context.getScreenInfo();
        int w = screen.width();
        int h = screen.height();

        context.renderFrame(g2d -> {
            UiUtils.clearScreen(g2d, w, h);
            ReservedCardsView.drawReservedCardsView(g2d, w, h , game.getCurrentPlayer().reservedCards());
            BoardView.drawBoard(g2d, w, h, game.board());
            PlayersView.drawPlayersView(g2d, w, h, game);
            BoughtCardsView.drawBoughtCardsView(g2d, w, h, game.getCurrentPlayer().cards());
        });
    }
}
