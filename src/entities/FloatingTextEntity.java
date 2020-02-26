package entities;

import main.Coordinates;

public class FloatingTextEntity {
    public String text;
    public Coordinates coordinates;
    public double movingSpeed = 2.0;
    public double timeLiving = 0;
    public double timeToLive = 800; // milliseconds
    public boolean dangerText = false;

    public FloatingTextEntity(double x, double y, String text, boolean xAxisRandomness, boolean yAxisRandomness, boolean dangerText) {
        int randomness = 10;
        if (xAxisRandomness) {
            x = x - (randomness / 2) + (int) (Math.random() * randomness);
        }
        if (yAxisRandomness) {
            y = y - (randomness / 2) + (int) (Math.random() * randomness);
        }
        new FloatingTextEntity(x, y, text, dangerText);
    }

    public FloatingTextEntity(double x, double y, String text, boolean dangerText) {
        this.text = text;
        this.coordinates = new Coordinates(x, y);
        this.dangerText = dangerText;
        FloatingText.addTextToList(this);
    }

    public boolean isDangerText() {
        return dangerText;
    }

    public void setDangerText(boolean dangerText) {
        this.dangerText = dangerText;
    }
}
