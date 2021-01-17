package entities;

import main.Coordinates;
import main.Texture;
import scene.Scene;

public class Tree extends StaticGraphicEntity {
    public static String ENTITY_CODE = "tree";

    public enum TreeType {
        TREE01(0), TREE02(1), TREE03(2), TREE04(3);

        public int value;

        TreeType(final int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public Sprite getSprite() {
            switch (this) {
                case TREE01:
                default:
                    return SpriteManager.getInstance().TREE01;
                case TREE02:
                    return SpriteManager.getInstance().TREE02;
                case TREE03:
                    return SpriteManager.getInstance().TREE03;
                case TREE04:
                    return SpriteManager.getInstance().TREE04;
            }
        }
    }

    public Tree(int x, int y, int t) {
        super(x, y);
        init(t);
    }

    private void init(int t) {
        this.type = t;
        setWorldCoordinates(Coordinates.tileCoordinatesToWorldCoordinates((int) getTileCoordinates().x, (int) getTileCoordinates().y));
        TreeType treeType = TreeType.values()[type];
        setSprite(treeType.getSprite());
        Collision collision;
        switch (treeType) {
            case TREE01:
            case TREE02:
            default:
                collision = new Collision(new Coordinates(getWorldCoordinates().x + 8, getWorldCoordinates().y - 8), 16, 16);
                break;
            case TREE03:
            case TREE04:
                collision = new Collision(new Coordinates(getWorldCoordinates().x + 16, getWorldCoordinates().y - 8), 16, 16);
                break;
        }
        setCollision(collision);   //Square collision
        Scene.getInstance().getListOfGraphicEntities().add(this);
        Scene.getInstance().getListOfStaticEntities().add(this);
    }

    @Override
    public void update(long timeElapsed) {
        if (getSprite().IDLE_FRAMES > 0) {
            double frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.005 + Math.random() * 0.01));
            setSpriteCoordinateFromSpriteSheetX(frame % getSprite().IDLE_FRAMES);
        }
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
