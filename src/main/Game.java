package main;

import audio.OpenALManager;
import scene.Camera;
import scene.Scene;
import listeners.InputListenerManager;
import ui.UserInterface;

public class Game {
    public static void startGame() {
        Strings.updateStrings(Strings.englishStringPath);
        Parameters.init();
        Window.init();
        Log.l("Project Version: " + Parameters.getProjectVersion());
        Scene.getInstance().init();
        GameStatus.setStatus(GameStatus.Status.RUNNING);
        Log.l("------------ GAME INITIATED! ------------");
    }

    public static void update(long timeElapsed) {
        long startTime = System.nanoTime();
        timeElapsed = timeElapsed / GameTime.getTimeSpeedFactor();
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

    public static void render() {
        long startTime = System.nanoTime();
        OpenGLManager.prepareFrame();
        OpenGLManager.updateShadersUniforms();

        Scene.getInstance().render();
        SpecialEffects.render();
        UserInterface.getInstance().render();
        FramesPerSecond.updateRenderingTimeNanoseconds(System.nanoTime() - startTime);
    }

    public static void stopGame() {
        OpenALManager.destroy();
        InputListenerManager.release();
        Log.closeLogFile();
    }
}

