public class Parameters {
    private static Parameters instance = null;

    private Parameters() {
    }

    public static Parameters getInstance() {
        if (instance == null) {
            return new Parameters();
        }
        return instance;
    }

    private static int framesPerSecond = 60;
    private static int windowWidth = 1280;
    private static int windowHeight = 720;

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

    public int getFramesPerSecond() {
        return framesPerSecond;
    }
}
