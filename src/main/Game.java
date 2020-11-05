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
//        long startTime = System.nanoTime();
        InputListenerManager.updateMouseWorldCoordinates();
        InputListenerManager.updateControllerInputs();
        Camera.getInstance().update(timeElapsed);
        Scene.getInstance().update(timeElapsed);
        Weather.getInstance().update(timeElapsed);
        GameTime.getInstance().update(timeElapsed);
        UserInterface.getInstance().update(timeElapsed);
//        Log.l("Time elapsed updating: " + (System.nanoTime() - startTime) + " nanoseconds");
    }

    public static void render(long timeElapsed) {
//        long startTime = System.nanoTime();
        OpenGLManager.prepareFrame();
        OpenGLManager.updateShadersUniforms();

        Scene.getInstance().render();
        SpecialEffects.render();
        UserInterface.getInstance().render(timeElapsed);
//        Log.l("Time elapsed rendering: " + (System.nanoTime() - startTime) + " nanoseconds");

        //After everything rendered...
//        Log.l("GPU calls: " + OpenGLManager.GPU_CALLS);
    }

    public static void stopGame() {
        OpenALManager.destroy();
        InputListenerManager.release();
        Log.closeLogFile();
    }
}

