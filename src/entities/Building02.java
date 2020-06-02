package entities;

import main.Coordinates;
import main.Texture;
import scene.Scene;

public class Building02 extends StaticGraphicEntity {
    public static byte ENTITY_CODE = 32;

    public Building02(int x, int y) {
        super(x, y);
        init();
    }

    private void init() {
        setWorldCoordinates(Coordinates.tileCoordinatesToWorldCoordinates((int) getTileCoordinates().x, (int) getTileCoordinates().y));
        setSprite(SpriteManager.getInstance().BUILDING02);
        setCollision(new Collision(new Coordinates(getWorldCoordinates().x + 48, getWorldCoordinates().y - 16), 96, 32));   //Square collision
        Scene.getInstance().getListOfEntities().add(this);
        Scene.getInstance().getListOfStaticEntities().add(this);
    }

    @Override
    public void update(long timeElapsed) {
        double frame;
        frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.005));
        setSpriteCoordinateFromSpriteSheetX(frame % getSprite().IDLE_FRAMES);
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
