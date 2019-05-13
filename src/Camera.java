public class Camera {
    private static Camera instance = null;
    int xCoordinate;
    int yCoordinate;

    Camera() {
        xCoordinate = 0;
        yCoordinate = 0;
    }

    public static Camera getInstance() {
        if (instance == null) {
            instance = new Camera();
        }
        return instance;
    }

    public int getxCoordinate() {
        return xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }

    public void setCoordinates(int x, int y) {
        xCoordinate = x;
        yCoordinate = y;
    }
}
