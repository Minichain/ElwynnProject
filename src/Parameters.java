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

    public int FRAMES_PER_SECOND = 60;
    public int WINDOW_WIDTH = 1280;
    public int WINDOW_HEIGHT = 720;
}
