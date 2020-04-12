package entities;

import main.Coordinates;
import main.Texture;
import scene.Scene;

public class Building01 extends StaticGraphicEntity {
    public static int ENTITY_CODE = 31;

    public Building01(int x, int y) {
        super(x, y);
        init();
    }

    private void init() {
        setWorldCoordinates(Coordinates.tileCoordinatesToWorldCoordinates((int) getTileCoordinates().x, (int) getTileCoordinates().y));
        setSprite(SpriteManager.getInstance().BUILDING01);
        setCollision(new Collision(new Coordinates(getWorldCoordinates().x + 32, getWorldCoordinates().y - 16), 64, 32));   //Square collision
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
