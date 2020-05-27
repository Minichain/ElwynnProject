package particles;

import entities.LightSource;
import main.Coordinates;
import main.OpenGLManager;
import scene.Camera;
import scene.Scene;

import java.awt.*;

public class Particle {
    private Coordinates center;
    private double[] velocityVector;
    private float size;
    private double movingSpeed;
    private double timeLiving = 0;
    private double timeToLive;
    private Color color;
    private boolean producesLight;
    private LightSource lightSource;
    private float intensity;

    public Particle(Coordinates center, double[] velocityVector, double movingSpeed, float size, Color color, boolean producesLight) {
        this(center, velocityVector, movingSpeed, size, color, 600.0, producesLight);
    }

    public Particle(Coordinates center, double[] velocityVector, double movingSpeed, float size, Color color, double timeToLive, boolean producesLight) {
        this(center, velocityVector, movingSpeed, size, color, timeToLive, producesLight, 15f);
    }

    public Particle(Coordinates center, double[] velocityVector, double movingSpeed, float size, Color color, double timeToLive, boolean producesLight, float intensity) {
        this.velocityVector = velocityVector;
        this.movingSpeed = movingSpeed;
        this.center = new Coordinates(center.x, center.y);
        this.size = size;
        this.color = color;
        this.timeToLive = timeToLive;
        this.producesLight = producesLight;
        this.intensity = intensity;
        if (producesLight) {
            Coordinates lightSourceCoordinates = new Coordinates(center.x, center.y);
            lightSource = new LightSource(lightSourceCoordinates, intensity, color);
            Scene.getInstance().getListOfLightSources().add(lightSource);
        }
    }

    public void update(long timeElapsed) {
        this.timeLiving += timeElapsed;
        this.center.x += velocityVector[0] * timeElapsed * movingSpeed;
        this.center.y += velocityVector[1] * timeElapsed * movingSpeed;
        if (producesLight) {
            this.lightSource.setWorldCoordinates(center);
            this.lightSource.setIntensity(intensity - intensity * (float) (timeLiving / timeToLive));
        }
    }

    public void render() {
        float halfSize = size / 2;
        Coordinates centerCameraCoordinates = center.toCameraCoordinates();
        float size = (float) (this.size * Camera.getZoom());
//        float size = (float) ((timeLiving / timeToLive) * this.size * Camera.getZoom());
        OpenGLManager.drawRectangle((int) (centerCameraCoordinates.x - halfSize), (int) (centerCameraCoordinates.y - halfSize),
                size, size, 1.0 - timeLiving / timeToLive, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
    }

    public boolean isDead() {
        return timeLiving > timeToLive;
    }

    public LightSource getLightSource() {
        return lightSource;
    }
}
