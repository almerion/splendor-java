package src;

import src.controller.Controller;

public class Main {
    public static void main(String[] args) {
        var controller = Controller.createController();
        controller.start();
    }
}
