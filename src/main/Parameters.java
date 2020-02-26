package main;

import audio.OpenALManager;
import menu.Resolution;

public class Parameters {
    private static int framesPerSecond = 60;
    private static boolean fullScreen = true;
    private static Resolution resolution = Resolution.RESOLUTION_1920_1080;
    private static boolean debugMode = false;
    private static float soundLevel = 0.1f;

    public static int getFramesPerSecond() {
        return framesPerSecond;
    }

    public static void setFramesPerSecond(int framesPerSecond) {
        Parameters.framesPerSecond = framesPerSecond;
    }

    public static int getResolutionWidth() {
        return resolution.getResolution()[0];
    }

    public static int getResolutionHeight() {
        return resolution.getResolution()[1];
    }

    public static void setResolution(Resolution resolution) {
        Parameters.resolution = resolution;
        Window.setWindowSize(resolution.getResolution()[0], resolution.getResolution()[1]);
    }

    public static boolean isFullScreen() {
        return fullScreen;
    }

    public static void setFullScreen(boolean fullScreen) {
        Parameters.fullScreen = fullScreen;
    }

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static void setDebugMode(boolean debugMode) {
        Parameters.debugMode = debugMode;
    }

    public static float getSoundLevel() {
        return soundLevel;
    }

    public static void setSoundLevel(float soundLevel) {
        OpenALManager.onSoundLevelChange(soundLevel);
        Parameters.soundLevel = soundLevel;
    }
}
