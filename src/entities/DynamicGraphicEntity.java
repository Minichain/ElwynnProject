package entities;

public abstract class DynamicGraphicEntity extends GraphicEntity {
    public double speed;
    public double[] movementVector;

    public DynamicGraphicEntity(int x, int y) {
        super(x, y);
        movementVector = new double[2];
    }
}
