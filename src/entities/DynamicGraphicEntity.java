package entities;

public abstract class DynamicGraphicEntity extends GraphicEntity {
    public double speed;
    public double[] movementVector;
    public double[] movementVectorNormalized;

    public DynamicGraphicEntity(int x, int y) {
        super(x, y);
        movementVector = new double[2];
    }
}
