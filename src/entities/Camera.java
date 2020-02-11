package entities;

import listeners.MyInputListener;
import main.Coordinates;
import main.GameMode;
import main.Parameters;
import utils.MathUtils;

public class Camera {
    private static Camera instance = null;
    private Coordinates coordinates;
    private static double xInitialCoordinate = Parameters.getInstance().getStartingCoordinates().x;
    private static double yInitialCoordinate = Parameters.getInstance().getStartingCoordinates().y;
    private static int width = 1720;
    private static int height = 720;
    private static double zoom = 2;

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

    public static double getZoom() {
        return zoom;
    }

    public static double getWidth() {
        return width / zoom;
    }

    public static double getHeight() {
        return height / zoom;
    }

    public static void setZoom(double zoom) {
        Camera.zoom = zoom;
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
            double cameraSpeed = MathUtils.module(cameraVelocityVector) * 0.0025 * timeElapsed;
            cameraVelocityVector = MathUtils.normalizeVector(cameraVelocityVector);

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