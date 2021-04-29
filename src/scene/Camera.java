package scene;

import entities.Player;
import listeners.ActionManager;
import main.Coordinates;
import main.GameMode;
import main.Parameters;
import utils.MathUtils;

public class Camera {
    private static Camera instance = null;
    private Coordinates coordinates;
    private static double zoom;
    private static double minZoom;
    private static double maxZoom;
    private static double freeCameraSpeed;
    private static double followingSpeed;

    public Camera() {
        init(new Coordinates(0, 0));
    }

    public void init(Coordinates c) {
        coordinates = c;
        minZoom = 1.0;
        maxZoom = 8.0;
        zoom = 4.0;
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

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates.x = coordinates.x;
        this.coordinates.y = coordinates.y;
    }

    public void setCoordinates(double x, double y) {
        coordinates.x = x;
        coordinates.y = y;
    }

    /**
     * Amount of pixels we are able to see in the X axis.
     * */
    public static double getWidth() {
        return Parameters.getResolutionWidth() / getZoom();
    }

    /**
     * Amount of pixels we are able to see in the Y axis.
     * */
    public static double getHeight() {
        return Parameters.getResolutionHeight() / getZoom();
    }

    public static double getZoom() {
        return zoom * Parameters.getHeightResolutionFactor();
    }

    public static void setZoom(double zoom) {
        if (zoom <= minZoom) {
            Camera.zoom = minZoom;
        } else if (zoom >= maxZoom) {
            Camera.zoom = maxZoom;
        } else {
            Camera.zoom = zoom;
        }
    }

    public static void increaseZoom() {
        setZoom(zoom + 1.0);
    }

    public static void decreaseZoom() {
        setZoom(zoom - 1.0);
    }

    public void update(long timeElapsed) {
        if (GameMode.getGameMode() == GameMode.Mode.NORMAL) {
            followingSpeed = 0.0015 * zoom;
            double[] cameraVelocityVector = new double[2];
            cameraVelocityVector[0] = Player.getInstance().getCenterOfMassWorldCoordinates().x - getCoordinates().x;
            cameraVelocityVector[1] = Player.getInstance().getCenterOfMassWorldCoordinates().y - getCoordinates().y;
            double cameraSpeed = MathUtils.module(cameraVelocityVector) * followingSpeed * timeElapsed;
            cameraVelocityVector = MathUtils.normalizeVector(cameraVelocityVector);

//            Log.l("ElwynGraphicsLog:: cameraVelocityVector: " + cameraVelocityVector[0] + ", " + cameraVelocityVector[1]);
//            Log.l("ElwynGraphicsLog:: cameraVelocityVector after applying cameraSpeed: " + cameraVelocityVector[0] * cameraSpeed + ", " + cameraVelocityVector[1] * cameraSpeed);
            if (Double.isNaN(cameraVelocityVector[0]) || Double.isNaN(cameraVelocityVector[1])) {
                return;
            }

            setCoordinates((getCoordinates().x + (cameraVelocityVector[0] * cameraSpeed)),
                    (getCoordinates().y + (cameraVelocityVector[1] * cameraSpeed)));

            if (shaking) {
                setCoordinates(getCoordinates().x + Math.random() * shakingIntensity - shakingIntensity / 2f,
                        getCoordinates().y + Math.random() * shakingIntensity - shakingIntensity / 2f);

                shakingTime -= timeElapsed;
                shaking = shakingTime >= 0;
            }
        } else {
            freeCameraSpeed = 2.0 / zoom;
            double[] movement = computeMovementVector(timeElapsed, freeCameraSpeed);
            setCoordinates((getCoordinates().x + (movement[0])),
                    (getCoordinates().y + (movement[1])));
        }
    }

    public double[] computeMovementVector(long timeElapsed, double speed) {
        double[] movement = new double[]{0, 0};

        if (ActionManager.MOVING_DOWN) {
            movement[1] += 1;
        }
        if (ActionManager.MOVING_LEFT) {
            movement[0] += -1;
        }
        if (ActionManager.MOVING_UP) {
            movement[1] += -1;
        }
        if (ActionManager.MOVING_RIGHT) {
            movement[0] += 1;
        }

        movement = MathUtils.normalizeVector(movement);
        movement[0] *= timeElapsed * speed;
        movement[1] *= timeElapsed * speed;

        return movement;
    }

    private boolean shaking = false;
    private int shakingTime = 100;    //milliseconds
    private float shakingIntensity;    //milliseconds

    public void shake(int shakingTime, float shakingIntensity) {
        this.shaking = true;
        this.shakingTime = shakingTime;
        this.shakingIntensity = shakingIntensity;
    }
}