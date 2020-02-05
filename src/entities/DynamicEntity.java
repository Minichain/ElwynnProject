package entities;

import main.Coordinates;
import main.MyOpenGL;
import main.Texture;

import static org.lwjgl.opengl.GL11.*;

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

    public void drawSprite(int x, int y) {
        getSpriteSheet().bind();

        float u = ((1f / X_SPRITES) * (int) getSpriteCoordinateFromSpriteSheetX());
        float v = 1f - ((1f / Y_SPRITES) * (int) getSpriteCoordinateFromSpriteSheetY());
        float u2 = u + (1f / X_SPRITES);
        float v2 = v - (1f / Y_SPRITES);
        double scale = Scene.getZoom();

        glBegin(GL_QUADS);
        x -= (SPRITE_WIDTH / 2) * (int) scale;
        y -= (SPRITE_HEIGHT / 2) * (int) scale;
        MyOpenGL.drawTexture(x, y , u, v, u2, v2, (int) (SPRITE_WIDTH * scale), (int) (SPRITE_HEIGHT * scale));
        glEnd();
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
