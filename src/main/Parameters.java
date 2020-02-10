package main;

public class Parameters {
    private static Parameters instance = null;
    private static int foregroundFramesPerSecond;
    private static int windowWidth;
    private static int windowHeight;
    private static long window;
    private static boolean debugMode;
    private static Coordinates startingCoordinates;

    private Parameters() {
        debugMode = false;
        foregroundFramesPerSecond = 60;
        windowWidth = 1920;
        windowHeight = 1080;
        startingCoordinates = new Coordinates(5000, 5000);
    }

    public static Parameters getInstance() {
        if (instance == null) {
            instance = new Parameters();
        }
        return instance;
    }

    public void setWindowWidth(int width) {
        windowWidth = width;
    }

    public void setWindowHeight(int height) {
        windowHeight = height;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public void setForegroundFramesPerSecond(int fps) {
        foregroundFramesPerSecond = fps;
    }

    public int getForegroundFramesPerSecond() {
        return foregroundFramesPerSecond;
    }

    public void setDebugMode(boolean dm) {
        debugMode = dm;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public Coordinates getStartingCoordinates() {
        return startingCoordinates;
    }

    public long getWindow() {
        return window;
    }

    public void setWindow(long w) {
        window = w;
    }
}
