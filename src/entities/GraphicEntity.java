package entities;

import main.Coordinates;
import main.Texture;

import java.util.ArrayList;

public abstract class GraphicEntity extends Entity {
    /** Sprite Attributes **/
    private Sprite sprite;
    private double spriteCoordinateFromSpriteSheetX;
    private double spriteCoordinateFromSpriteSheetY;
    private Coordinates centerOfMassWorldCoordinates;
    private Coordinates centerOfMassCameraCoordinates;
    private ArrayList<LightSource> lightSources;
    public int type;

    public GraphicEntity(double x, double y) {
        super(x, y);
        centerOfMassWorldCoordinates = new Coordinates(getWorldCoordinates().x, getWorldCoordinates().y);
        centerOfMassCameraCoordinates = new Coordinates(getCameraCoordinates().x, getCameraCoordinates().y);
        lightSources = new ArrayList<>();
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
        centerOfMassWorldCoordinates = new Coordinates(getWorldCoordinates().x + (double) getSprite().SPRITE_WIDTH / 2.0,
                getWorldCoordinates().y - (double) getSprite().SPRITE_HEIGHT / 4.0);
        centerOfMassCameraCoordinates = centerOfMassWorldCoordinates.toCameraCoordinates();
    }

    public abstract String getEntityCode();

    public boolean isOverEntity(Coordinates coordinates) {
        return (Math.abs(coordinates.x - centerOfMassWorldCoordinates.x) <= (getSprite().SPRITE_WIDTH / 2))
                && (Math.abs(coordinates.y - centerOfMassWorldCoordinates.y) <= (getSprite().SPRITE_HEIGHT / 2));
    }

    public ArrayList<LightSource> getLightSources() {
        return lightSources;
    }

    public int getType() {
        return type;
    }

    public abstract void drawHitBox();
}
