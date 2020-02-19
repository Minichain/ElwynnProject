package main;

public class Parameters {
    private static int framesPerSecond = 60;
    private static boolean fullScreen = false;
    private static int resolutionWidth = 1920;
    private static int resolutionHeight = 1080;
    private static boolean debugMode = false;

    public static int getFramesPerSecond() {
        return framesPerSecond;
    }

    public static void setFramesPerSecond(int framesPerSecond) {
        Parameters.framesPerSecond = framesPerSecond;
    }

    public static int getResolutionWidth() {
        return resolutionWidth;
    }

    public static void setResolutionWidth(int resolutionWidth) {
        Parameters.resolutionWidth = resolutionWidth;
    }

    public static int getResolutionHeight() {
        return resolutionHeight;
    }

    public static void setResolutionHeight(int resolutionHeight) {
        Parameters.resolutionHeight = resolutionHeight;
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
}
