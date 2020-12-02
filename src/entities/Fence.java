package entities;

import main.Coordinates;
import main.Texture;
import scene.Scene;

public class Fence extends StaticGraphicEntity {
    public static byte ENTITY_CODE = 2;

    public enum FenceType {
        FENCE01(0), FENCE02(1), FENCE03(2), FENCE04(3);

        public int value;

        FenceType(final int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public Sprite getSprite() {
            switch (this) {
                case FENCE01:
                default:
                    return SpriteManager.getInstance().FENCE01;
                case FENCE02:
                    return SpriteManager.getInstance().FENCE02;
                case FENCE03:
                    return SpriteManager.getInstance().FENCE03;
                case FENCE04:
                    return SpriteManager.getInstance().FENCE04;
            }
        }
    }

    public Fence(int x, int y, int t) {
        super(x, y);
        init(t);
    }

    private void init(int t) {
        this.type = t;
        setWorldCoordinates(Coordinates.tileCoordinatesToWorldCoordinates((int) getTileCoordinates().x, (int) getTileCoordinates().y));
        FenceType treeType = FenceType.values()[type];
        setSprite(treeType.getSprite());
        setCollision(new Collision(new Coordinates(getWorldCoordinates().x + 8, getWorldCoordinates().y - 8), 16, 16));   //Square collision
        Scene.getInstance().getListOfEntities().add(this);
        Scene.getInstance().getListOfStaticEntities().add(this);
    }

    @Override
    public void update(long timeElapsed) {
 
    }

    @Override
    public Texture getSpriteSheet() {
        return getSprite().getSpriteSheet();
    }

    @Override
    public byte getEntityCode() {
        return ENTITY_CODE;
    }
}
