package entities;

import main.OpenGLManager;
import main.Texture;
import scene.Camera;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glEnd;

public abstract class GraphicEntity extends Entity {
    /** Sprite Attributes **/
    private double spriteCoordinateFromSpriteSheetX;
    private double spriteCoordinateFromSpriteSheetY;
    public int SPRITE_WIDTH;
    public int SPRITE_HEIGHT;
    public int IDLE_FRAMES;
    public int RUNNING_FRAMES;
    public int DYING_FRAMES;
    public int DEAD_FRAMES;

    public GraphicEntity(int x, int y) {
        super(x, y);
        loadSprite();
    }

    public abstract void loadSprite();

    public void drawSprite(int x, int y, Texture spriteSheet) {
        spriteSheet.bind();

        float width = spriteSheet.getWidth() / SPRITE_WIDTH;
        float height = spriteSheet.getHeight() / SPRITE_HEIGHT;

        float u = ((1f / width) * (int) getSpriteCoordinateFromSpriteSheetX());
        float v = 1f - ((1f / height) * (int) getSpriteCoordinateFromSpriteSheetY());
        float u2 = u + (1f / width);
        float v2 = v - (1f / height);
        double scale = Camera.getZoom();

        OpenGLManager.glBegin(GL_QUADS);
        x -= (int) ((SPRITE_WIDTH / 2) * scale);
        y -= (int) ((SPRITE_HEIGHT / 2) * scale);
        OpenGLManager.drawTexture(x, y, u, v, u2, v2, (float) (SPRITE_WIDTH * scale), (float) (SPRITE_HEIGHT * scale));
        glEnd();
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

    public abstract Texture getSpriteSheet();
}
