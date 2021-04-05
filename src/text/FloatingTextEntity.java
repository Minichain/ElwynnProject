package text;

import main.Coordinates;
import main.Parameters;
import utils.MathUtils;

import java.awt.*;

public class FloatingTextEntity {
    private String text;
    private Coordinates coordinates;
    private double movingSpeed;
    private double[] movingVector;
    private double timeLiving = 0;
    private double timeToLive = 800; // milliseconds
    private Color color;
    private float scale;

    public FloatingTextEntity(double x, double y, String text, Color color, double movingSpeed, double[] movingVector, float scale) {
        float randomness = 5f;
        x = x - MathUtils.random(-randomness, randomness);
        y = y - MathUtils.random(-randomness, randomness);
        this.text = text;
        this.coordinates = new Coordinates(x, y);
        this.color = color;
        this.movingSpeed = movingSpeed;
        this.movingVector = movingVector;
        this.scale = scale;
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
                Parameters.getHeightResolutionFactor() * this.scale, true, alpha, color.getRed(), color.getGreen(), color.getBlue());
    }

    private void onDestroy() {
        FloatingText.getListOfFloatingTextEntities().remove(this);
    }
}
