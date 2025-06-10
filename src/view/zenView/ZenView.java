package src.view.zenView;

import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.PointerEvent;
import src.model.Game;
import src.model.Player;
import src.model.utils.BoardType;
import src.utils.Action;
import src.utils.DropTokensAction;
import src.view.View;
import src.view.zenView.actions.ActionsView;
import src.view.zenView.actions.DropActionView;
import src.view.zenView.game.GameView;

import java.awt.*;
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

    public static KeyboardEvent waitForUserKeyPress(ApplicationContext context, long timeoutMs) {
        CompletableFuture<KeyboardEvent> future = new CompletableFuture<>();

        long endTime = System.currentTimeMillis() + timeoutMs;
        while ((timeoutMs == Long.MAX_VALUE || System.currentTimeMillis() < endTime) && !future.isDone()) {
            try {
                var event = context.pollOrWaitEvent(100);
                if (event == null) continue;

                switch (event) {
                    case KeyboardEvent keyboardEvent -> {
                        if (keyboardEvent.action() == KeyboardEvent.Action.KEY_RELEASED)
                            continue;
                        future.complete(keyboardEvent);
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
    public Action readPlayerAction(Game game) {
        Objects.requireNonNull(game);
        var actionsView = ActionsView.create(context, game);
        actionsView.drawActionsView();
        return actionsView.getUserAction();
    }

    @Override
    public void invalidActionMessage(Action action) {
        Objects.requireNonNull(action);
        var screen = context.getScreenInfo();
        int w = screen.width();
        int h = screen.height();
        context.renderFrame(g2d -> {
            var rectWidth = 200;
            var rectHeight = 50;
            var x = (w / 2) - (rectWidth / 2);
            var y = (h / 2) - (rectHeight / 2);
            var rect = new Rectangle(x, y, rectWidth, rectHeight);
            var oldColor = g2d.getColor();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(x, y, rectWidth, rectHeight);
            g2d.setColor(Color.RED);
            UiUtils.centerTextInRect(g2d, "Action invalide", rect);
            g2d.setColor(oldColor);
        });

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void displayWin(Game game) {
        Objects.requireNonNull(game);
        var screen = context.getScreenInfo();
        int w = screen.width();
        int h = screen.height();
        context.renderFrame(g2d -> {
            var oldColor = g2d.getColor();
            g2d.setColor(Color.GREEN);
            var rectWidth = 300;
            var rectHeight = 100;
            var x = (w / 2) - (rectWidth / 2);
            var y = (h / 2) - (rectHeight / 2);
            var rect = new Rectangle(x, y, rectWidth, rectHeight);
            g2d.fillRect(x, y, rectWidth, rectHeight);
            g2d.setColor(Color.BLACK);
            UiUtils.centerTextInRect(g2d, "Le joueur numéro " + (game.getWinnerIndex() + 1)  + " a gagné !", rect);
            g2d.setColor(oldColor);
        });

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public DropTokensAction readDropTokensAction(Player currentPlayer) {
        Objects.requireNonNull(currentPlayer);
        var dropTokensView = new DropActionView(context);
        dropTokensView.drawDropAction();
        return dropTokensView.readDropTokensAction();
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
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
