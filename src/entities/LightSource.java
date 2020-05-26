package entities;

import main.Coordinates;

public class LightSource {
    private Coordinates worldCoordinates;
    private Coordinates cameraCoordinates;
    private float intensity;
    private long timeOn;
    private float[] color;

    public LightSource(Coordinates worldCoordinates, float intensity) {
        this(worldCoordinates, intensity, new float[]{1f, 1f, 1f});
    }

    public LightSource(Coordinates worldCoordinates, float intensity, float[] color) {
        this.worldCoordinates = worldCoordinates;
        this.cameraCoordinates = worldCoordinates.toCameraCoordinates();
        this.intensity = intensity;
        this.timeOn = 0;
        this.color = color;
    }

    public Coordinates getWorldCoordinates() {
        return worldCoordinates;
    }

    public void setWorldCoordinates(Coordinates worldCoordinates) {
        this.worldCoordinates = worldCoordinates;
    }

    public Coordinates getCameraCoordinates() {
        return cameraCoordinates;
    }

    public void update(long timeElapsed) {
        //Update Camera coordinates
        cameraCoordinates = worldCoordinates.toCameraCoordinates();
        timeOn += timeElapsed;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public float[] getColor() {
        return color;
    }
}
