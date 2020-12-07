package main;

import audio.OpenALManager;
import scene.Camera;
import scene.Scene;
import listeners.InputListenerManager;

public class Game {
    public static void startGame() {
        Parameters.init();
        Window.init();
        Log.l("Project Version: " + Parameters.getProjectVersion());
        Scene.getInstance().init();
        GameStatus.setStatus(GameStatus.Status.RUNNING);
        Log.l("------------ GAME INITIATED! ------------");
    }

    public static void update(long timeElapsed) {
        long startTime = System.nanoTime();
        GameStatus.setRuntime(GameStatus.getRuntime() + timeElapsed);
        InputListenerManager.updateMouseWorldCoordinates();
        InputListenerManager.updateControllerInputs();
        Camera.getInstance().update(timeElapsed);
        Scene.getInstance().update(timeElapsed);
        Weather.getInstance().update(timeElapsed);
        GameTime.getInstance().update(timeElapsed);
        UserInterface.getInstance().update(timeElapsed);
        FramesPerSecond.updateUpdatingTimeNanoseconds(System.nanoTime() - startTime);
    }

    public static void render(long timeElapsed) {
        long startTime = System.nanoTime();
        OpenGLManager.prepareFrame();
        OpenGLManager.updateShadersUniforms();

        Scene.getInstance().render();
        SpecialEffects.render();
        UserInterface.getInstance().render(timeElapsed);
        FramesPerSecond.updateRenderingTimeNanoseconds(System.nanoTime() - startTime);
    }

    public static void stopGame() {
        OpenALManager.destroy();
        InputListenerManager.release();
        Log.closeLogFile();
    }
}

