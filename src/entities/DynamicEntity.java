package entities;

import main.Coordinates;
import main.Texture;

public abstract class DynamicEntity extends Entity {
    private Coordinates previousCoordinates;

    /** Variables **/
    public float HEALTH;
    public double SPEED;
    public double[] DISPLACEMENT_VECTOR;

    /** Sprite Attributes **/
    private Texture spriteSheet;
    private double spriteCoordinateFromSpriteSheetX;
    private double spriteCoordinateFromSpriteSheetY;
    public int X_SPRITES;
    public int Y_SPRITES;
    public int SPRITE_WIDTH;
    public int SPRITE_HEIGHT;
    public int IDLE_FRAMES;
    public int RUNNING_FRAMES;
    public int DYING_FRAMES;
    public int DEAD_FRAMES;

    public DynamicEntity(int x, int y, int prevX, int prevY) {
        super(x, y);
        previousCoordinates = new Coordinates(prevX, prevY);
        DISPLACEMENT_VECTOR = new double[2];
    }

    public Coordinates getCurrentCoordinates() {
        return getCoordinates();
    }

    public Coordinates getPreviousCoordinates() {
        return previousCoordinates;
    }

    public Texture getSpriteSheet() {
        return spriteSheet;
    }

    public void setSpriteSheet(Texture spriteSheet) {
        this.spriteSheet = spriteSheet;
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
}
