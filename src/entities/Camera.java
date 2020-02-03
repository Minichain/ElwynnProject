package entities;

import listeners.MyInputListener;
import main.Coordinates;
import main.GameMode;
import main.Parameters;
import main.Utils;

public class Camera {
    private static Camera instance = null;
    private Coordinates coordinates;
    private static double xInitialCoordinate = Parameters.getInstance().getStartingCoordinates().x;
    private static double yInitialCoordinate = Parameters.getInstance().getStartingCoordinates().y;

    public Camera() {
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

    public void setCoordinates(double x, double y) {
        coordinates.x = x;
        coordinates.y = y;
    }

    public void resetCamera() {
        this.setCoordinates((int) Character.getInstance().getCurrentCoordinates().x,
                (int) Character.getInstance().getCurrentCoordinates().y);
    }

    public void update(long timeElapsed) {
        if (GameMode.getInstance().getGameMode() == GameMode.Mode.NORMAL) {
            double[] cameraVelocityVector = new double[2];
            cameraVelocityVector[0] = Character.getInstance().getCurrentCoordinates().x - Camera.getInstance().getCoordinates().x;
            cameraVelocityVector[1] = Character.getInstance().getCurrentCoordinates().y - Camera.getInstance().getCoordinates().y;
            double cameraSpeed = Utils.module(cameraVelocityVector) * 0.0025 * timeElapsed;
            cameraVelocityVector = Utils.normalizeVector(cameraVelocityVector);

//            System.out.println("ElwynGraphicsLog:: cameraVelocityVector: " + cameraVelocityVector[0] + ", " + cameraVelocityVector[1]);
//            System.out.println("ElwynGraphicsLog:: cameraVelocityVector after applying cameraSpeed: " + cameraVelocityVector[0] * cameraSpeed + ", " + cameraVelocityVector[1] * cameraSpeed);
            if (Double.isNaN(cameraVelocityVector[0]) || Double.isNaN(cameraVelocityVector[1])) {
                return;
            }

            Camera.getInstance().setCoordinates((int) (Camera.getInstance().getCoordinates().x + (cameraVelocityVector[0] * cameraSpeed)),
                    (int)(Camera.getInstance().getCoordinates().y + (cameraVelocityVector[1] * cameraSpeed)));
        } else {
            double[] movement = MyInputListener.computeMovementVector(timeElapsed, 1.0);
            Camera.getInstance().setCoordinates((int) (Camera.getInstance().getCoordinates().x + (movement[0])),
                    (int)(Camera.getInstance().getCoordinates().y + (movement[1])));
        }
    }
}