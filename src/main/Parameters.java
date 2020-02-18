package main;

public class Parameters {
    private static int framesPerSecond = 60;
    private static long window = -1;
    private static boolean fullScreen = false;
    private static int resolutionWidth = 1280;
    private static int resolutionHeight = 720;
    private static int windowWidth = 1280;
    private static int windowHeight = 720;
    private static boolean debugMode = false;

    public static int getFramesPerSecond() {
        return framesPerSecond;
    }

    public static void setFramesPerSecond(int framesPerSecond) {
        Parameters.framesPerSecond = framesPerSecond;
    }

    public static long getWindow() {
        return window;
    }

    public static void setWindow(long WINDOW) {
        Parameters.window = WINDOW;
    }

    public static int getWindowWidth() {
        return windowWidth;
    }

    public static void setWindowWidth(int windowWidth) {
        Parameters.windowWidth = windowWidth;
    }

    public static int getWindowHeight() {
        return windowHeight;
    }

    public static void setWindowHeight(int windowHeight) {
        Parameters.windowHeight = windowHeight;
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

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static void setDebugMode(boolean debugMode) {
        Parameters.debugMode = debugMode;
    }
}
