package entities;

import main.Coordinates;
import main.Texture;
import scene.Scene;

public class Tree04 extends StaticGraphicEntity {
    public static byte ENTITY_CODE = 14;

    public Tree04(int x, int y) {
        super(x, y);
        init();
    }

    private void init() {
        setWorldCoordinates(Coordinates.tileCoordinatesToWorldCoordinates((int) getTileCoordinates().x, (int) getTileCoordinates().y));
        setSprite(SpriteManager.getInstance().TREE04);
        setCollision(new Collision(new Coordinates(getWorldCoordinates().x + 16, getWorldCoordinates().y - 8), 16, 16));   //Square collision
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
