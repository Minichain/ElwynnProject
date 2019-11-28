package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import listeners.MyInputListener;
import main.Coordinates;
import main.Utils;

public class Camera extends OrthographicCamera {
    private static Camera instance = null;
    private static Coordinates coordinates;

    public Camera(int width, int height) {
        super(width, height);
        coordinates = new Coordinates(0, 0);
        coordinates.setxCoordinate((int) Character.getInstance().getCurrentCoordinates().getxCoordinate());
        coordinates.setyCoordinate((int) Character.getInstance().getCurrentCoordinates().getyCoordinate());
        position.set((int) Character.getInstance().getCurrentCoordinates().getxCoordinate(),
                (int) Character.getInstance().getCurrentCoordinates().getyCoordinate(), 0);
    }

    public static Camera getInstance() {
        if (instance == null) {
            instance = new Camera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        return instance;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(int x, int y) {
        coordinates.setxCoordinate(x);
        coordinates.setyCoordinate(y);
        position.set(x, y, 0);
    }

    public void resetCamera() {
        this.setCoordinates((int) Character.getInstance().getCurrentCoordinates().getxCoordinate(),
                (int) Character.getInstance().getCurrentCoordinates().getyCoordinate());
    }

    public void update(long timeElapsed) {
        double[] cameraVelocityVector = new double[2];
        cameraVelocityVector[0] = Character.getInstance().getCurrentCoordinates().getxCoordinate() - this.getCoordinates().getxCoordinate();
        cameraVelocityVector[1] = Character.getInstance().getCurrentCoordinates().getyCoordinate() - this.getCoordinates().getyCoordinate();
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

        this.setCoordinates((int) (this.getCoordinates().getxCoordinate() + (cameraVelocityVector[0] * cameraSpeed)),
                (int) (this.getCoordinates().getyCoordinate() + (cameraVelocityVector[1] * cameraSpeed)));
        this.zoom = 1.0f + ((float) MyInputListener.getInstance().getMouseWheelPosition() / 50f);
        update();
    }
}
