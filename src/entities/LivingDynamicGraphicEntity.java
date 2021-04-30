package entities;

public abstract class LivingDynamicGraphicEntity extends DynamicGraphicEntity {
    public float health;
    public double[] facingVector = new double[]{0, 0};

    public LivingDynamicGraphicEntity(double x, double y) {
        super(x, y);
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public abstract void onDying();

    public abstract void hurt(float damage);
}
