package src.view.zenView;

import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.PointerEvent;
import src.model.Game;
import src.model.Player;
import src.model.utils.BoardType;
import src.utils.Action;
import src.utils.DropTokensAction;
import src.view.View;
import src.view.zenView.game.GameView;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ZenView implements View {
    private final ApplicationContext context;
    private final GameView gameView;

    public ZenView(ApplicationContext context) {
        Objects.requireNonNull(context);
        this.context = context;
        this.gameView = new GameView(context);
    }

    public static PointerEvent waitForUserClick(ApplicationContext context, long timeoutMs) {
        CompletableFuture<PointerEvent> future = new CompletableFuture<>();

        long endTime = System.currentTimeMillis() + timeoutMs;
        while ((timeoutMs == Long.MAX_VALUE || System.currentTimeMillis() < endTime) && !future.isDone()) {
            try {
                var event = context.pollOrWaitEvent(100);
                if (event == null) continue;

                switch (event) {
                    case PointerEvent pointerEvent -> {
                        if (pointerEvent.action() == PointerEvent.Action.POINTER_UP)
                            continue;
                        future.complete(pointerEvent);
                    }
                    default -> {}
                }
            } catch (Exception e) {
                // Gestion des exceptions
            }
        }

        try {
            return future.getNow(null);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void displayGameState(Game game) {
        Objects.requireNonNull(game);
        gameView.displayGameState(game);
    }

    @Override
    public BoardType getBoardType() {
        var boardTypeView = new BoardTypeView(context);
        return boardTypeView.getBoardType();
    }

    @Override
    public int getNumberOfPlayers() {
        var PlayerCountView = new PlayerCountView(context);
        return PlayerCountView.getNumberOfPlayers();
    }

    @Override
    public Action readPlayerAction(Player player) {
        ZenView.waitForUserClick(context, Long.MAX_VALUE);
        return null;
    }

    @Override
    public void invalidActionMessage(Action action) {
        Objects.requireNonNull(action);
        var screen = context.getScreenInfo();
        int w = screen.width();
        int h = screen.height();
        context.renderFrame(g2d -> {
            UiUtils.centerText(g2d, "Action invalide", w, h);
        });

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void displayWin(Player winner) {

    }

    @Override
    public DropTokensAction readDropTokensAction(Player currentPlayer) {
        return null;
    }

    @Override
    public void invalidNumberOfPlayersMessage(int numberOfPlayers) {
        var screen = context.getScreenInfo();
        int w = screen.width();
        int h = screen.height();
        context.renderFrame(g2d -> {
            UiUtils.centerText(g2d, "Nombre de joueurs invalide : " + numberOfPlayers, w, h);
        });
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
