public class Parameters {
    private static Parameters instance = null;

    Parameters() {
    }

    public static Parameters getInstance() {
        if (instance == null) {
            return new Parameters();
        }
        return instance;
    }

    public int framesPerSecond = 60;
    public int windowWidth = 1280;
    public int windowHeight = 720;

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
