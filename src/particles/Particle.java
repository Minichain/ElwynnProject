package particles;

import main.Coordinates;
import main.OpenGLManager;
import scene.Camera;

public class Particle {
    private Coordinates center;
    private double[] velocityVector;
    private float size;
    private double movingSpeed = 0.25;
    private double timeLiving = 0;
    private double timeToLive = 600;
    private float r;
    private float g;
    private float b;

    public Particle(Coordinates center, double[] velocityVector, float size, float r, float g, float b) {
        this.velocityVector = velocityVector;
        this.center = center;
        this.size = size;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void update(long timeElapsed) {
        this.timeLiving += timeElapsed;
        this.center.x += velocityVector[0] * timeElapsed * movingSpeed;
        this.center.y += velocityVector[1] * timeElapsed * movingSpeed;
    }

    public void render() {
        float halfSize = size / 2;
        Coordinates centerCameraCoordinates = center.toCameraCoordinates();
        float size = (float) (this.size * Camera.getZoom());
//        float size = (float) ((timeLiving / timeToLive) * this.size * Camera.getZoom());
        OpenGLManager.drawRectangle((int) (centerCameraCoordinates.x - halfSize), (int) (centerCameraCoordinates.y - halfSize), size, size, 1.0 - timeLiving / timeToLive, r, g, b);
    }

    public boolean isDead() {
        return timeLiving > timeToLive;
    }
}
