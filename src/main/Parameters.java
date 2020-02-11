package main;

public class Parameters {
    private static int framesPerSecond = 60;
    private static long window = -1;
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

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static void setDebugMode(boolean debugMode) {
        Parameters.debugMode = debugMode;
    }
}
