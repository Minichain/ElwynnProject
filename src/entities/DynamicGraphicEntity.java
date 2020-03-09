package entities;

import main.Coordinates;

public abstract class DynamicGraphicEntity extends GraphicEntity {
    private Coordinates previousWorldCoordinates;

    /** Variables **/
    public float health;
    public double speed;
    public double[] displacementVector;
    public double[] facingVector;

    public DynamicGraphicEntity(int x, int y, int prevX, int prevY) {
        super(x, y);
        previousWorldCoordinates = new Coordinates(prevX, prevY);
        displacementVector = new double[2];
    }

    public Coordinates getPreviousWorldCoordinates() {
        return previousWorldCoordinates;
    }

    public void setPreviousWorldCoordinates(Coordinates coordinates) {
        previousWorldCoordinates.x = coordinates.x;
        previousWorldCoordinates.y = coordinates.y;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }
}
