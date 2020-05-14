package entities;

import main.Coordinates;

public class LightSource {
    private Coordinates worldCoordinates;
    private Coordinates cameraCoordinates;
    private float intensity;

    public LightSource(Coordinates worldCoordinates) {
        this.worldCoordinates = worldCoordinates;
        this.cameraCoordinates = worldCoordinates.toCameraCoordinates();
        this.intensity = 1f;
    }

    public Coordinates getWorldCoordinates() {
        return worldCoordinates;
    }

    public Coordinates getCameraCoordinates() {
        return cameraCoordinates;
    }

    public void update() {
        //Update Camera coordinates
        cameraCoordinates = worldCoordinates.toCameraCoordinates();
    }
}
