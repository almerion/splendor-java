package src.view.zenView;

import java.awt.*;
import java.util.Objects;

public record MenuButton(String label, Rectangle rect) {
    public MenuButton {
        Objects.requireNonNull(label);
        Objects.requireNonNull(rect);
    }
}
