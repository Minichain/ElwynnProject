package entities;

import main.Coordinates;
import main.Texture;
import scene.Scene;

public class Building extends StaticGraphicEntity {
    public static byte ENTITY_CODE = 3;

    public enum BuildingType {
        BUILDING01(0), BUILDING02(1);

        public int value;

        BuildingType(final int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public Sprite getSprite() {
            switch (this) {
                case BUILDING01:
                default:
                    return SpriteManager.getInstance().BUILDING01;
                case BUILDING02:
                    return SpriteManager.getInstance().BUILDING02;
            }
        }
    }

    public Building(int x, int y, int t) {
        super(x, y);
        init(t);
    }

    private void init(int t) {
        this.type = t;
        BuildingType buildingType = BuildingType.values()[t];
        setWorldCoordinates(Coordinates.tileCoordinatesToWorldCoordinates((int) getTileCoordinates().x, (int) getTileCoordinates().y));
        setSprite(buildingType.getSprite());
        Collision collision;
        switch (buildingType) {
            case BUILDING01:
            default:
                collision = new Collision(new Coordinates(getWorldCoordinates().x + 32, getWorldCoordinates().y - 16), 64, 32);
                break;
            case BUILDING02:
                collision = new Collision(new Coordinates(getWorldCoordinates().x + 48, getWorldCoordinates().y - 16), 96, 32);
                break;
        }
        setCollision(collision);
        Scene.getInstance().getListOfGraphicEntities().add(this);
        Scene.getInstance().getListOfStaticEntities().add(this);
    }

    @Override
    public void update(long timeElapsed) {
        if (getSprite().IDLE_FRAMES > 0) {
            double frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.005));
            setSpriteCoordinateFromSpriteSheetX(frame % getSprite().IDLE_FRAMES);
        }
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
