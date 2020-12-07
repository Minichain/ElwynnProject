package entities;

public abstract class LivingDynamicGraphicEntity extends DynamicGraphicEntity {
    public float health;
    public double[] facingVector;

    public LivingDynamicGraphicEntity(double x, double y) {
        super(x, y);
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
        if (this.health <= 0) {
            onDying();
        }
    }

    public abstract void onDying();

    public abstract void hurt(float damage);
}
