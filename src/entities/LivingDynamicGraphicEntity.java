package entities;

import main.Texture;

public class LivingDynamicGraphicEntity extends DynamicGraphicEntity {
    public float health;
    public double[] facingVector;

    public LivingDynamicGraphicEntity(int x, int y) {
        super(x, y);
    }

    @Override
    public void update(long timeElapsed) {

    }

    @Override
    public Texture getSpriteSheet() {
        return null;
    }

    @Override
    public void drawSprite(int x, int y) {

    }

    @Override
    public byte getEntityCode() {
        return 0;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }
}
