package entities;

import main.Coordinates;
import main.Texture;
import scene.Scene;

public class Warp extends StaticGraphicEntity {
    public static String ENTITY_CODE = "warp";

    public enum WarpType {
        WARP01(0), WARP02(1), WARP03(2), WARP04(3);

        public int value;

        WarpType(final int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public Sprite getSprite() {
            switch (this) {
                case WARP01:
                default:
                    return SpriteManager.getInstance().WARP_UP_LARGE;
                case WARP02:
                case WARP03:
                case WARP04:
                    return SpriteManager.getInstance().WARP_DOWN_LARGE;
            }
        }
    }

    public Warp(int x, int y, int t) {
        super(x, y);
        init(t);
    }

    private void init(int t) {
        this.type = t;
        setWorldCoordinates(Coordinates.tileCoordinatesToWorldCoordinates((int) getTileCoordinates().x, (int) getTileCoordinates().y));
        WarpType warpType = WarpType.values()[type];
        setSprite(warpType.getSprite());
        setCollision(null);
        Scene.getInstance().getListOfGraphicEntities().add(this);
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
    public String getEntityCode() {
        return ENTITY_CODE;
    }

}
