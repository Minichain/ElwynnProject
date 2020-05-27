package entities;

import main.Coordinates;
import main.Texture;
import scene.Scene;

public class Light01 extends StaticGraphicEntity {
    public static byte ENTITY_CODE = 51;

    public Light01(int x, int y) {
        super(x, y);
        init();
    }

    private void init() {
        setWorldCoordinates(Coordinates.tileCoordinatesToWorldCoordinates((int) getTileCoordinates().x, (int) getTileCoordinates().y));
        setSprite(SpriteManager.getInstance().LIGHT01);
        setCollision(new Collision(new Coordinates(getWorldCoordinates().x + 8, getWorldCoordinates().y - 8), 16, 16));   //Square collision
        Scene.getInstance().getListOfEntities().add(this);
        Scene.getInstance().getListOfStaticEntities().add(this);

        /** LIGHT SOURCES **/
        Coordinates lightSourceCoordinates = new Coordinates(getWorldCoordinates().x + 8, getWorldCoordinates().y - 58);
        float intensity = 170f;
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
    public byte getEntityCode() {
        return ENTITY_CODE;
    }
}
