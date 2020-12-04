package entities;

import main.Coordinates;
import main.Texture;
import scene.Scene;

import java.awt.*;

public class Torch extends StaticGraphicEntity {
    public static byte ENTITY_CODE = 5;

    public Torch(int x, int y) {
        super(x, y);
        init();
    }

    private void init() {
        setWorldCoordinates(Coordinates.tileCoordinatesToWorldCoordinates((int) getTileCoordinates().x, (int) getTileCoordinates().y));
        setSprite(SpriteManager.getInstance().TORCH01);
        setCollision(new Collision(new Coordinates(getWorldCoordinates().x + 8, getWorldCoordinates().y - 8), 8, 8));   //Square collision
        Scene.getInstance().getListOfEntities().add(this);
        Scene.getInstance().getListOfStaticEntities().add(this);

        /** LIGHT SOURCES **/
        Coordinates lightSourceCoordinates = new Coordinates(getWorldCoordinates().x + 8, getWorldCoordinates().y - getSprite().SPRITE_HEIGHT + 16);
        float intensity = 75f;
        getLightSources().add(new LightSource(lightSourceCoordinates, intensity, new Color(255, 240, 170)));
        for (LightSource lightSource : getLightSources()) {
            Scene.getInstance().getListOfLightSources().add(lightSource);
        }
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
