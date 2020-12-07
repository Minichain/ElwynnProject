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

    public static void update(long timeElapsedNanos) {
        long startTime = System.nanoTime();
        long timeElapsedMillis = timeElapsedNanos / 1000000;
        GameStatus.setRuntime(GameStatus.getRuntime() + timeElapsedMillis);
        InputListenerManager.updateMouseWorldCoordinates();
        InputListenerManager.updateControllerInputs();
        Camera.getInstance().update(timeElapsedMillis);
        Scene.getInstance().update(timeElapsedMillis);
        Weather.getInstance().update(timeElapsedMillis);
        GameTime.getInstance().update(timeElapsedMillis);
        UserInterface.getInstance().update(timeElapsedNanos);
        FramesPerSecond.updateUpdatingTimeNanoseconds(System.nanoTime() - startTime);
    }

    public static void render(long timeElapsed) {
        long startTime = System.nanoTime();
        long timeElapsedMillis = timeElapsed / 1000000;
        OpenGLManager.prepareFrame();
        OpenGLManager.updateShadersUniforms();

        Scene.getInstance().render();
        SpecialEffects.render();
        UserInterface.getInstance().render(timeElapsedMillis);
        FramesPerSecond.updateRenderingTimeNanoseconds(System.nanoTime() - startTime);
    }

    public static void stopGame() {
        OpenALManager.destroy();
        InputListenerManager.release();
        Log.closeLogFile();
    }
}

