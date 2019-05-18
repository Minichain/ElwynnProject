package entities;

import main.Coordinates;

public class Camera {
    private static Camera instance = null;
    Coordinates coordinates;

    Camera() {
        coordinates = new Coordinates(0, 0);
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
}
