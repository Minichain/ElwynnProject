package entities;

import main.Coordinates;
import main.Texture;

public abstract class GraphicEntity extends Entity {
    /** Sprite Attributes **/
    private Sprite sprite;
    private double spriteCoordinateFromSpriteSheetX;
    private double spriteCoordinateFromSpriteSheetY;
    private Coordinates centerOfMassWorldCoordinates;
    private Coordinates centerOfMassCameraCoordinates;

    public GraphicEntity(int x, int y) {
        super(x, y);
        centerOfMassWorldCoordinates = new Coordinates(getWorldCoordinates().x, getWorldCoordinates().y);
        centerOfMassCameraCoordinates = new Coordinates(getCameraCoordinates().x, getCameraCoordinates().y);
    }

    public double getSpriteCoordinateFromSpriteSheetX() {
        return spriteCoordinateFromSpriteSheetX;
    }

    public void setSpriteCoordinateFromSpriteSheetX(double spriteCoordinateFromSpriteSheetX) {
        this.spriteCoordinateFromSpriteSheetX = spriteCoordinateFromSpriteSheetX;
    }

    public double getSpriteCoordinateFromSpriteSheetY() {
        return spriteCoordinateFromSpriteSheetY;
    }

    public void setSpriteCoordinateFromSpriteSheetY(double spriteCoordinateFromSpriteSheetY) {
        this.spriteCoordinateFromSpriteSheetY = spriteCoordinateFromSpriteSheetY;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public abstract Texture getSpriteSheet();

    public abstract void drawSprite(int x, int y);

    public Coordinates getCenterOfMassWorldCoordinates() {
        return centerOfMassWorldCoordinates;
    }

    public Coordinates getCenterOfMassCameraCoordinates() {
        return centerOfMassCameraCoordinates;
    }

    @Override
    public void updateCoordinates() {
        super.updateCoordinates();
        centerOfMassWorldCoordinates = new Coordinates(getWorldCoordinates().x + (double) Player.getInstance().getSprite().SPRITE_WIDTH / 2.0,
                getWorldCoordinates().y - (double) Player.getInstance().getSprite().SPRITE_HEIGHT / 2.0);
        centerOfMassCameraCoordinates = centerOfMassWorldCoordinates.toCameraCoordinates();
    }

    public abstract int getEntityCode();
}
