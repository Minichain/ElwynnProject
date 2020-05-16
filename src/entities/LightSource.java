package entities;

import main.Coordinates;

public class LightSource {
    private Coordinates worldCoordinates;
    private Coordinates cameraCoordinates;
    private float intensity;
    private long timeOn;

    public LightSource(Coordinates worldCoordinates, float intensity) {
        this.worldCoordinates = worldCoordinates;
        this.cameraCoordinates = worldCoordinates.toCameraCoordinates();
        this.intensity = intensity;
        this.timeOn = (long) (Math.random() * 100.0);
    }

    public Coordinates getWorldCoordinates() {
        return worldCoordinates;
    }

    public Coordinates getCameraCoordinates() {
        return cameraCoordinates;
    }

    public void update(long timeElapsed) {
        //Update Camera coordinates
        cameraCoordinates = worldCoordinates.toCameraCoordinates();
        intensity = intensity + (float) (Math.sin(timeOn) * intensity * 0.01);
        timeOn += timeElapsed;
    }

    public float getIntensity() {
        return intensity;
    }
}
