package entities;

import main.OpenGLManager;
import main.Texture;
import scene.Camera;
import scene.Scene;

public class Smoke extends GraphicEntity {
    public int timeToLive;
    public int timeLiving;

    public Smoke(int x, int y, int timeToLive) {
        super(x, y);
        init(timeToLive);
    }

    public void init(int timeToLive) {
        this.timeToLive = timeToLive;
        this.timeLiving = 0;
        setSprite(SpriteManager.getInstance().SMOKE01);
        Scene.getInstance().getListOfGraphicEntities().add(this);
    }

    @Override
    public Texture getSpriteSheet() {
        return getSprite().getSpriteSheet();
    }

    @Override
    public String getEntityCode() {
        return null;
    }

    @Override
    public void update(long timeElapsed) {
        timeLiving += timeElapsed;
        if (timeLiving >= timeToLive) {
            setDead(true);
        } else {
            double frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * ((double) getSprite().IDLE_FRAMES / (double) timeToLive)));
            setSpriteCoordinateFromSpriteSheetX(frame % getSprite().IDLE_FRAMES);
        }
    }

    @Override
    public void drawSprite(int x, int y) {
        getSprite().draw(x - getSprite().SPRITE_WIDTH / 2, y - getSprite().SPRITE_HEIGHT / 2,
                (int) getSpriteCoordinateFromSpriteSheetX(), (int) getSpriteCoordinateFromSpriteSheetY(),
                0.75f, Camera.getZoom() * 0.5f);
    }

    @Override
    public void drawHitBox() {
        int width = (int) (getSprite().SPRITE_WIDTH * Camera.getZoom());
        int height = (int) ((-1) * getSprite().SPRITE_HEIGHT * Camera.getZoom());
//        OpenGLManager.drawRectangleOutline((float) getCameraCoordinates().x, (float) getCameraCoordinates().y, width, height);
        OpenGLManager.drawLine((float) getCameraCoordinates().x, (float) getCameraCoordinates().y, width, 0, 1,0f, 0f, 1f);
        OpenGLManager.drawLine((float) getCameraCoordinates().x, (float) getCameraCoordinates().y, 0, height, 1,0f, 1f, 0f);
    }
}
