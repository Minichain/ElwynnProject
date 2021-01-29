package entities;

import main.Coordinates;
import main.Texture;
import scene.Scene;

public class UtilityPole extends StaticGraphicEntity{
    public static String ENTITY_CODE = "utility_pole";

    public UtilityPole(int x, int y) {
        super(x, y);
        init();
    }

    private void init() {
        setWorldCoordinates(Coordinates.tileCoordinatesToWorldCoordinates(getTileCoordinates()[0], getTileCoordinates()[1]));
        setSprite(SpriteManager.getInstance().UTILITY_POLE_01);
        setCollision(new Collision(new Coordinates(getWorldCoordinates().x + 8, getWorldCoordinates().y - 4), 8));
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
