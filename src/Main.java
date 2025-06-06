package src;

import com.github.forax.zen.Application;
import src.controller.Controller;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
        var rect = new Rectangle(-100, -100, -100, -100);
        Application.run(Color.BLACK, (ctx) -> {
            var controller = Controller.createZenController(ctx);
            controller.start();
        });
    }
}
