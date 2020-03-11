package entities;

import main.Coordinates;
import main.Texture;
import scene.Scene;

public class Building extends StaticGraphicEntity {

    public Building(int x, int y) {
        super(x, y);
        init();
    }

    private void init() {
        setWorldCoordinates(Coordinates.tileCoordinatesToWorldCoordinates((int) getTileCoordinates().x, (int) getTileCoordinates().y));
        setSprite(SpriteManager.getInstance().BUILDING);
        Scene.getInstance().getListOfEntities().add(this);
    }

    @Override
    public void update(long timeElapsed) {
        
    }

    @Override
    public Texture getSpriteSheet() {
        return getSprite().getSpriteSheet();
    }

    @Override
    public void drawSprite(int x, int y) {
        getSprite().draw(x, y, (int) getSpriteCoordinateFromSpriteSheetX(), (int) getSpriteCoordinateFromSpriteSheetY(), 1.0);
    }
}
