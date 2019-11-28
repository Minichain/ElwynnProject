package main;

public class Parameters {
    private static Parameters instance = null;
    private static int foregroundFramesPerSecond;
    private static int backgroundFramesPerSecond;
    private static int windowWidth;
    private static int windowHeight;
    private static boolean debugMode;
    private static int tilesSizeX;
    private static int tilesSizeY;
    private static Coordinates startingCoordinates;

    private Parameters() {
        debugMode = false;
        foregroundFramesPerSecond = 60;
        backgroundFramesPerSecond = 30;
        windowWidth = 1280;
        windowHeight = 720;
        tilesSizeX = 64;
        tilesSizeY = 64;
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

    public void setBackgroundFramesPerSecond(int fps) {
        backgroundFramesPerSecond = fps;
    }

    public int getBackgroundFramesPerSecond() {
        return backgroundFramesPerSecond;
    }

    public void setDebugMode(boolean dm) {
        debugMode = dm;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public int getTilesSizeX() {
        return tilesSizeX;
    }

    public int getTilesSizeY() {
        return tilesSizeY;
    }

    public Coordinates getStartingCoordinates() {
        return startingCoordinates;
    }
}
