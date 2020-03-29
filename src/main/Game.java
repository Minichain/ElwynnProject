package main;

import audio.OpenALManager;
import scene.Camera;
import scene.Scene;
import listeners.InputListenerManager;
import scene.TileMap;

public class Game {
    public static void startGame() {
        Window.start();
        Scene.getInstance();    //Initialize Scene
        TileMap.loadMap();
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

