package text;

import main.Coordinates;
import main.Parameters;

import java.awt.*;

public class FloatingTextEntity {
    public String text;
    public Coordinates coordinates;
    public double movingSpeed;
    public double[] movingVector;
    public double timeLiving = 0;
    public double timeToLive = 800; // milliseconds
    public Color color;

    public FloatingTextEntity(double x, double y, String text, Color color, double movingSpeed, double[] movingVector) {
        float randomness = 10;
        x = x - (randomness / 2f) + (int) (Math.random() * randomness);
        y = y - (randomness / 2f) + (int) (Math.random() * randomness);
        this.text = text;
        this.coordinates = new Coordinates(x, y);
        this.color = color;
        this.movingSpeed = movingSpeed;
        this.movingVector = movingVector;
        FloatingText.addTextToList(this);
    }

    public void update(long timeElapsed) {
        if (this.timeLiving < this.timeToLive) {
            this.timeLiving += timeElapsed;
            this.coordinates = new Coordinates(this.coordinates.x + (this.movingVector[0] * this.movingSpeed),
                    this.coordinates.y + (this.movingVector[1] * this.movingSpeed));
        } else {
            onDestroy();
        }
    }

    public void render() {
        float alpha = 1f - (float) (this.timeLiving / this.timeToLive);
        Coordinates entityCameraCoordinates = this.coordinates.toCameraCoordinates();
        TextRendering.renderText((int) entityCameraCoordinates.x, (int) entityCameraCoordinates.y, this.text,
                Parameters.getResolutionFactor() * 2f, true, alpha, color.getRed(), color.getGreen(), color.getBlue());
    }

    private void onDestroy() {
        FloatingText.getListOfFloatingTextEntities().remove(this);
    }
}
