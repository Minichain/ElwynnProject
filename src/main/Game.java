package main;

import audio.OpenALManager;
import scene.Camera;
import scene.Scene;
import listeners.InputListenerManager;

public class Game {
    public static void startGame() {
        Window.start();
        Scene.getInstance();    //Initialize Scene
        GameStatus.setStatus(GameStatus.Status.RUNNING);
    }

    public static void update(long timeElapsed) {
        InputListenerManager.updateMouseWorldCoordinates();
        Camera.getInstance().update(timeElapsed);
        Scene.getInstance().update(timeElapsed);
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

