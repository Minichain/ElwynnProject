package main;

import audio.OpenALManager;
import entities.Camera;
import entities.Scene;
import listeners.InputListenerManager;

public class Game {
    public static void startGame() {
        Window.start();
        GameStatus.setStatus(GameStatus.Status.RUNNING);
    }

    public static void update(long timeElapsed) {
        Scene.getInstance().update(timeElapsed);
        Camera.getInstance().update(timeElapsed);
        InputListenerManager.updateMouseWorldCoordinates();
    }

    public static void render(long timeElapsed) {
        OpenGLManager.prepareFrame();
        Scene.getInstance().render();
        SpecialEffects.render();
        UserInterface.getInstance().render(timeElapsed);
    }

    public static void stopGame() {
        OpenALManager.destroy();
        InputListenerManager.release();
    }
}

