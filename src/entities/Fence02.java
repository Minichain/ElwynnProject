package entities;

import main.Coordinates;
import main.Texture;
import scene.Scene;

public class Fence02 extends StaticGraphicEntity {
    public static int ENTITY_CODE = 22;

    public Fence02(int x, int y) {
        super(x, y);
        init();
    }

    private void init() {
        setWorldCoordinates(Coordinates.tileCoordinatesToWorldCoordinates((int) getTileCoordinates().x, (int) getTileCoordinates().y));
        setSprite(SpriteManager.getInstance().FENCE02);
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
    public int getEntityCode() {
        return ENTITY_CODE;
    }
}
