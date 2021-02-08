package entities;

import main.Coordinates;
import main.OpenGLManager;
import scene.Camera;

public abstract class StaticGraphicEntity extends GraphicEntity {
    private int[] tileCoordinates;
    private Collision collision;

    public StaticGraphicEntity(int x, int y) {
        super(x, y);
        tileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(x, y);
    }

    public int[] getTileCoordinates() {
        return tileCoordinates;
    }

    public void setCollision(Collision collision) {
        this.collision = collision;
    }

    public Collision getCollision() {
        return collision;
    }

    public void drawSprite(int x, int y) {
        getSprite().draw(x, y, (int) getSpriteCoordinateFromSpriteSheetX(), (int) getSpriteCoordinateFromSpriteSheetY(), 1f);
    }

    @Override
    public void drawHitBox() {
        int width = (int) (getSprite().SPRITE_WIDTH * Camera.getZoom());
        int height = (int) ((-1) * getSprite().SPRITE_HEIGHT * Camera.getZoom());
//        OpenGLManager.drawRectangleOutline((float) getCameraCoordinates().x, (float) getCameraCoordinates().y, width, height);
        OpenGLManager.drawLine((float) getCameraCoordinates().x, (float) getCameraCoordinates().y, width, 0, 1,0f, 0f, 1f);
        OpenGLManager.drawLine((float) getCameraCoordinates().x, (float) getCameraCoordinates().y, 0, height, 1,0f, 1f, 0f);
        if (getCollision() != null) getCollision().draw();
    }
}