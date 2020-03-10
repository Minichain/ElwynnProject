package entities;

import main.Texture;

public abstract class GraphicEntity extends Entity {
    /** Sprite Attributes **/
    private double spriteCoordinateFromSpriteSheetX;
    private double spriteCoordinateFromSpriteSheetY;

    public GraphicEntity(int x, int y) {
        super(x, y);
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

    public abstract Sprite getSprite();

    public abstract Texture getSpriteSheet();

    public abstract void drawSprite(int x, int y);
}
