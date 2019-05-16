public class Parameters {
    private static Parameters instance = null;
    private static int framesPerSecond;
    private static int windowWidth;
    private static int windowHeight;
    private static boolean debugMode;

    private Parameters() {
        debugMode = false;
        framesPerSecond = 60;
        windowWidth = 1280;
        windowHeight = 720;
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

    public void setFramesPerSecond(int fps) {
        framesPerSecond = fps;
    }

    public int getFramesPerSecond() {
        return framesPerSecond;
    }

    public void setDebugMode(boolean dm) {
        debugMode = dm;
    }

    public boolean isDebugMode() {
        return debugMode;
    }
}
