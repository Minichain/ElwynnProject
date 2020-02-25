package entities;

import main.Coordinates;
import main.MyOpenGL;
import main.Texture;

import static org.lwjgl.opengl.GL11.*;

public abstract class DynamicEntity extends Entity {
    private Coordinates previousCoordinates;

    /** Variables **/
    public float health;
    public double speed;
    public double[] displacementVector;
    public double[] facingVector;

    /** Sprite Attributes **/
    private double spriteCoordinateFromSpriteSheetX;
    private double spriteCoordinateFromSpriteSheetY;
    public int SPRITE_WIDTH;
    public int SPRITE_HEIGHT;
    public int IDLE_FRAMES;
    public int RUNNING_FRAMES;
    public int DYING_FRAMES;
    public int DEAD_FRAMES;

    public DynamicEntity(int x, int y, int prevX, int prevY) {
        super(x, y);
        previousCoordinates = new Coordinates(prevX, prevY);
        displacementVector = new double[2];
    }

    public void drawSprite(int x, int y, Texture spriteSheet) {
        spriteSheet.bind();

        float width = spriteSheet.getWidth() / SPRITE_WIDTH;
        float height = spriteSheet.getHeight() / SPRITE_HEIGHT;

        float u = ((1f / width) * (int) getSpriteCoordinateFromSpriteSheetX());
        float v = 1f - ((1f / height) * (int) getSpriteCoordinateFromSpriteSheetY());
        float u2 = u + (1f / width);
        float v2 = v - (1f / height);
        double scale = Camera.getZoom();

        glBegin(GL_QUADS);
        x -= (int) ((SPRITE_WIDTH / 2) * scale);
        y -= (int) ((SPRITE_HEIGHT / 2) * scale);
        MyOpenGL.drawTexture(x, y, u, v, u2, v2, (float) (SPRITE_WIDTH * scale), (float) (SPRITE_HEIGHT * scale));
        glEnd();
    }

    public Coordinates getCurrentCoordinates() {
        return getCoordinates();
    }

    public Coordinates getPreviousCoordinates() {
        return previousCoordinates;
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

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }
}
