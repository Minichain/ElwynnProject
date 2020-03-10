package entities;

import main.Coordinates;
import main.Texture;
import scene.Scene;
import scene.TileMap;

public class Tree extends StaticGraphicEntity {

    public Tree(int x, int y) {
        super(x, y);
        init();
    }

    private void init() {
        setWorldCoordinates(Coordinates.tileCoordinatesToWorldCoordinates((int) getTileCoordinates().x, (int) getTileCoordinates().y));
        TileMap.getArrayOfTiles()[(int) getTileCoordinates().x][(int) getTileCoordinates().y].setCollidable(true);
        Scene.getInstance().getListOfEntities().add(this);
    }

    @Override
    public void update(long timeElapsed) {

    }

    @Override
    public Sprite getSprite() {
        return SpriteManager.getInstance().TREE;
    }

    @Override
    public Texture getSpriteSheet() {
        return getSprite().getSpriteSheet();
    }

    @Override
    public void drawSprite(int x, int y) {
        getSprite().draw(x, y, (int) getSpriteCoordinateFromSpriteSheetX(), (int) getSpriteCoordinateFromSpriteSheetY());
    }
}
