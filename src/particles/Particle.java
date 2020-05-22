package particles;

import entities.LightSource;
import main.Coordinates;
import main.OpenGLManager;
import scene.Camera;
import scene.Scene;

public class Particle {
    private Coordinates center;
    private double[] velocityVector;
    private float size;
    private double movingSpeed = 0.25;
    private double timeLiving = 0;
    private double timeToLive;
    private float r;
    private float g;
    private float b;
    private boolean producesLight;
    private LightSource lightSource;

    public Particle(Coordinates center, double[] velocityVector, float size, float r, float g, float b, boolean producesLight) {
        this(center, velocityVector, size, r, g, b, 600.0, producesLight);
    }

    public Particle(Coordinates center, double[] velocityVector, float size, float r, float g, float b, double timeToLive, boolean producesLight) {
        this.velocityVector = velocityVector;
        this.center = center;
        this.size = size;
        this.r = r;
        this.g = g;
        this.b = b;
        this.timeToLive = timeToLive;
        this.producesLight = producesLight;
        if (producesLight) {
            Coordinates lightSourceCoordinates = new Coordinates(center.x, center.y);
            float intensity = 0.05f;
            lightSource = new LightSource(lightSourceCoordinates, intensity, new float[]{r, g, b});
            Scene.getInstance().getListOfLightSources().add(lightSource);
        }
    }

    public void update(long timeElapsed) {
        this.timeLiving += timeElapsed;
        this.center.x += velocityVector[0] * timeElapsed * movingSpeed;
        this.center.y += velocityVector[1] * timeElapsed * movingSpeed;
        if (producesLight) {
            this.lightSource.setWorldCoordinates(center);
            this.lightSource.setIntensity(0.05f - 0.05f * (float) (timeLiving / timeToLive));
        }
    }

    public void render() {
        float halfSize = size / 2;
        Coordinates centerCameraCoordinates = center.toCameraCoordinates();
        float size = (float) (this.size * Camera.getZoom());
//        float size = (float) ((timeLiving / timeToLive) * this.size * Camera.getZoom());
        OpenGLManager.drawRectangle((int) (centerCameraCoordinates.x - halfSize), (int) (centerCameraCoordinates.y - halfSize),
                size, size, 1.0 - timeLiving / timeToLive, r, g, b);
    }

    public boolean isDead() {
        return timeLiving > timeToLive;
    }

    public LightSource getLightSource() {
        return lightSource;
    }
}
