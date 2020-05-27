package entities;

import main.Coordinates;

import java.awt.*;

public class LightSource {
    private Coordinates worldCoordinates;
    private Coordinates cameraCoordinates;
    private float intensity;
    private long timeOn;
    private Color color;

    public LightSource(Coordinates worldCoordinates, float intensity) {
        this(worldCoordinates, intensity, new Color(1f, 1f, 1f));
    }

    public LightSource(Coordinates worldCoordinates, float intensity, Color color) {
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

    public Color getColor() {
        return color;
    }
}
