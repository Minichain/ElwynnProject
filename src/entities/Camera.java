package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import listeners.MyInputListener;
import main.Coordinates;
import main.Utils;

public class Camera extends OrthographicCamera {
    private static Camera instance = null;
    private static Coordinates coordinates;
    private static float initialZoom = 0.5f;

    public Camera(int width, int height) {
        super(width, height);
        position.set((int) Character.getInstance().getCurrentCoordinates().x,
                (int) Character.getInstance().getCurrentCoordinates().y, 0);
        zoom = initialZoom;
    }

    public static Camera getInstance() {
        if (instance == null) {
            instance = new Camera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        return instance;
    }

    public Coordinates getCoordinates() {
        return new Coordinates(this.position.x, this.position.y);      //FIXME
    }

    public void setCoordinates(int x, int y) {
        position.set(x, y, 0);
    }

    public void resetCamera() {
        this.position.x = (int) Character.getInstance().getCurrentCoordinates().x;
        this.position.y = (int) Character.getInstance().getCurrentCoordinates().y;
    }

    public void update(long timeElapsed) {
        double[] cameraVelocityVector = new double[2];
        cameraVelocityVector[0] = Character.getInstance().getCurrentCoordinates().x - this.position.x;
        cameraVelocityVector[1] = Character.getInstance().getCurrentCoordinates().y - this.position.y;
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

        this.position.x += (cameraVelocityVector[0] * cameraSpeed);
        this.position.y += (cameraVelocityVector[1] * cameraSpeed);

        this.zoom = initialZoom + ((float) MyInputListener.getInstance().mouseWheelPosition / 50f);
        update();
    }
}
