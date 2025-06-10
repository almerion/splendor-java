package src;

import com.github.forax.zen.Application;
import src.controller.Controller;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("Please provide a board type argument: --base or --text");
        }

        switch (args[0].toLowerCase()) {
            case "--base":
                Application.run(Color.BLACK, (ctx) -> {
                    var controller = Controller.createZenController(ctx);
                    controller.start();
                });
                break;
            case "--text":
                var controller = Controller.createTerminalController();
                controller.start();
                break;
            default:
                throw new IllegalArgumentException("Invalid board type argument. Use --base or --text.");
        }
    }
}
