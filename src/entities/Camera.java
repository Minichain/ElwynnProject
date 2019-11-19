package entities;

import main.Coordinates;

public class Camera {
    private static Camera instance = null;
    Coordinates coordinates;
    private static int xInitialCoordinate = 1000;
    private static int yInitialCoordinate = 1000;

    Camera() {
        coordinates = new Coordinates(xInitialCoordinate, yInitialCoordinate);
    }

    public static Camera getInstance() {
        if (instance == null) {
            instance = new Camera();
        }
        return instance;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(int x, int y) {
        coordinates.setxCoordinate(x);
        coordinates.setyCoordinate(y);
    }

    public void resetCamera() {
        this.setCoordinates((int) Character.getInstance().getCurrentCoordinates().getxCoordinate(),
                (int) Character.getInstance().getCurrentCoordinates().getyCoordinate());
    }
}
