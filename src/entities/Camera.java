package entities;

import listeners.MyInputListener;
import main.Coordinates;
import main.GameMode;
import main.Parameters;
import utils.MathUtils;

public class Camera {
    private static Camera instance = null;
    private Coordinates coordinates;
    private static double xInitialCoordinate = Scene.getInitialCoordinates().x;
    private static double yInitialCoordinate = Scene.getInitialCoordinates().y;
    private static double zoom = 2;
    private static double freeCameraSpeed = 1.0;
    private static double followingSpeed = 0.0025;

    public Camera() {
        coordinates = new Coordinates(xInitialCoordinate, yInitialCoordinate);
    }

    public static Camera getInstance() {
        if (instance == null) {
            instance = new Camera();
        }
        return instance;
    }

    /**
     * Coordinates where the Camera is centered
     * */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double x, double y) {
        coordinates.x = x;
        coordinates.y = y;
    }

    /**
     * Amount of pixels we are able to see in the X axis.
     * */
    public static double getWidth() {
        return Parameters.getWindowWidth() / zoom;
    }

    /**
     * Amount of pixels we are able to see in the Y axis.
     * */
    public static double getHeight() {
        return Parameters.getWindowHeight() / zoom;
    }

    public static double getZoom() {
        return zoom;
    }

    public static void setZoom(double zoom) {
        Camera.zoom = zoom;
    }

    public void resetCamera() {
        this.setCoordinates((int) Character.getInstance().getCurrentCoordinates().x,
                (int) Character.getInstance().getCurrentCoordinates().y);
    }

    public void update(long timeElapsed) {
        if (GameMode.getGameMode() == GameMode.Mode.NORMAL) {
            double[] cameraVelocityVector = new double[2];
            cameraVelocityVector[0] = Character.getInstance().getCurrentCoordinates().x - Camera.getInstance().getCoordinates().x;
            cameraVelocityVector[1] = Character.getInstance().getCurrentCoordinates().y - Camera.getInstance().getCoordinates().y;
            double cameraSpeed = MathUtils.module(cameraVelocityVector) * followingSpeed * timeElapsed;
            cameraVelocityVector = MathUtils.normalizeVector(cameraVelocityVector);

//            System.out.println("ElwynGraphicsLog:: cameraVelocityVector: " + cameraVelocityVector[0] + ", " + cameraVelocityVector[1]);
//            System.out.println("ElwynGraphicsLog:: cameraVelocityVector after applying cameraSpeed: " + cameraVelocityVector[0] * cameraSpeed + ", " + cameraVelocityVector[1] * cameraSpeed);
            if (Double.isNaN(cameraVelocityVector[0]) || Double.isNaN(cameraVelocityVector[1])) {
                return;
            }

            Camera.getInstance().setCoordinates((Camera.getInstance().getCoordinates().x + (cameraVelocityVector[0] * cameraSpeed)),
                    (Camera.getInstance().getCoordinates().y + (cameraVelocityVector[1] * cameraSpeed)));
        } else {
            double[] movement = MyInputListener.computeMovementVector(timeElapsed, freeCameraSpeed);
            Camera.getInstance().setCoordinates((Camera.getInstance().getCoordinates().x + (movement[0])),
                    (Camera.getInstance().getCoordinates().y + (movement[1])));
        }
    }
}