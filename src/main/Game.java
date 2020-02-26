package main;

import audio.OpenALManager;
import entities.Camera;
import entities.Scene;

public class Game {
    public static void startGame() {
        Window.start();
        GameStatus.setStatus(GameStatus.Status.RUNNING);
    }

    public static void update(long timeElapsed) {
        if (GameStatus.getStatus() == GameStatus.Status.RUNNING) {
            Scene.getInstance().update(timeElapsed);
        }
        Camera.getInstance().update(timeElapsed);
    }

    public static void render(long timeElapsed) {
        MyOpenGL.prepareFrame();
        Scene.getInstance().render();
        SpecialEffects.render();
        UserInterface.getInstance().render(timeElapsed);
    }

    public static void stopGame() {
        OpenALManager.destroy();
    }
}

