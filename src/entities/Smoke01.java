package entities;

import main.Texture;
import scene.Camera;
import scene.Scene;

public class Smoke01 extends GraphicEntity {
    public int timeToLive;
    public int timeLiving;

    public Smoke01(int x, int y, int timeToLive) {
        super(x, y);
        init(timeToLive);
    }

    public void init(int timeToLive) {
        this.timeToLive = timeToLive;
        this.timeLiving = 0;
        setSprite(SpriteManager.getInstance().SMOKE01);
        Scene.getInstance().getListOfEntities().add(this);
    }

    @Override
    public Texture getSpriteSheet() {
        return getSprite().getSpriteSheet();
    }

    @Override
    public byte getEntityCode() {
        return 0;
    }

    @Override
    public void update(long timeElapsed) {
        timeLiving += timeElapsed;
        if (timeLiving >= timeToLive) {
            onDestroy();
        } else {
            double frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * ((double) getSprite().IDLE_FRAMES / (double) timeToLive)));
            setSpriteCoordinateFromSpriteSheetX(frame % getSprite().IDLE_FRAMES);
        }
    }

    @Override
    public void drawSprite(int x, int y) {
        getSprite().draw(x - getSprite().SPRITE_WIDTH / 2, y - getSprite().SPRITE_HEIGHT / 2,
                (int) getSpriteCoordinateFromSpriteSheetX(), (int) getSpriteCoordinateFromSpriteSheetY(),
                0.25f, Camera.getZoom() * 0.5f);
    }

    public void onDestroy() {
        Scene.getInstance().getListOfEntities().remove(this);
    }
}
