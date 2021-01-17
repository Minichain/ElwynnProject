package entities;

import main.Coordinates;
import main.Texture;
import scene.Scene;

public class Light extends StaticGraphicEntity {
    public static String ENTITY_CODE = "light";

    public Light(int x, int y) {
        super(x, y);
        init();
    }

    private void init() {
        setWorldCoordinates(Coordinates.tileCoordinatesToWorldCoordinates((int) getTileCoordinates().x, (int) getTileCoordinates().y));
        setSprite(SpriteManager.getInstance().LIGHT01);
        setCollision(new Collision(new Coordinates(getWorldCoordinates().x + 12, getWorldCoordinates().y - 4), 8));   //Square collision
        Scene.getInstance().getListOfGraphicEntities().add(this);
        Scene.getInstance().getListOfStaticEntities().add(this);

        /** LIGHT SOURCES **/
        Coordinates lightSourceCoordinates = new Coordinates(getWorldCoordinates().x + 6, getWorldCoordinates().y - getSprite().SPRITE_HEIGHT + 5);
        float intensity = 75f;
        getLightSources().add(new LightSource(lightSourceCoordinates, intensity));
        for (LightSource lightSource : getLightSources()) {
            Scene.getInstance().getListOfLightSources().add(lightSource);
        }
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
