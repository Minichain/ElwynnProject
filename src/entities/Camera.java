package entities;

import main.Coordinates;
import main.Utils;

public class Camera {
    private static Camera instance = null;
    Coordinates coordinates;
    private static int xInitialCoordinate = 5000;
    private static int yInitialCoordinate = 5000;

    private Camera() {
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

    public void update(long timeElapsed) {
        double[] cameraVelocityVector = new double[2];
        cameraVelocityVector[0] = Character.getInstance().getCurrentCoordinates().getxCoordinate() - Camera.getInstance().getCoordinates().getxCoordinate();
        cameraVelocityVector[1] = Character.getInstance().getCurrentCoordinates().getyCoordinate() - Camera.getInstance().getCoordinates().getyCoordinate();
        double cameraSpeed = Utils.module(cameraVelocityVector) * 0.0025 * timeElapsed;
        if (cameraSpeed > 1000) { //Too much speed?
            cameraSpeed = 0;
        }
        cameraVelocityVector = Utils.normalizeVector(cameraVelocityVector);

//        System.out.println("ElwynGraphicsLog:: cameraVelocityVector: " + cameraVelocityVector[0] + ", " + cameraVelocityVector[1]);
//        System.out.println("ElwynGraphicsLog:: cameraVelocityVector after applying cameraSpeed: " + cameraVelocityVector[0] * cameraSpeed + ", " + cameraVelocityVector[1] * cameraSpeed);
        if (Double.isNaN(cameraVelocityVector[0]) || Double.isNaN(cameraVelocityVector[1])) {
            return;
        }

        Camera.getInstance().setCoordinates((int) (Camera.getInstance().getCoordinates().getxCoordinate() + (cameraVelocityVector[0] * cameraSpeed)),
                (int)(Camera.getInstance().getCoordinates().getyCoordinate() + (cameraVelocityVector[1] * cameraSpeed)));
    }
}
