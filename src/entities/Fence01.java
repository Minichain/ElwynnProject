package entities;

import main.Coordinates;
import main.Texture;
import scene.Scene;

public class Fence01 extends StaticGraphicEntity {
    public static byte ENTITY_CODE = 21;

    public Fence01(int x, int y) {
        super(x, y);
        init();
    }

    private void init() {
        setWorldCoordinates(Coordinates.tileCoordinatesToWorldCoordinates((int) getTileCoordinates().x, (int) getTileCoordinates().y));
        setSprite(SpriteManager.getInstance().FENCE01);
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
