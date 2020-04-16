package entities;

import main.OpenGLManager;
import main.Texture;
import scene.Camera;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class Sprite {
    private Texture spriteSheet;

    public int SPRITE_WIDTH;
    public int SPRITE_HEIGHT;

    public int IDLE_FRAMES;
    public int RUNNING_FRAMES;
    public int DYING_FRAMES;
    public int DEAD_FRAMES;
    public int JUMPING_FRAMES;

    public int TILES_IN_TILESET_X_AXIS;
    public int TILES_IN_TILESET_Y_AXIS;

    public Sprite(String path, int spriteWidth, int spriteHeight, int idleFrames, int runningFrames, int dyingFrames, int deadFrames, int jumpingFrames) {
        spriteSheet = Texture.loadTexture(path);

        SPRITE_WIDTH = spriteWidth;
        SPRITE_HEIGHT = spriteHeight;

        IDLE_FRAMES = idleFrames;
        RUNNING_FRAMES = runningFrames;
        DYING_FRAMES = dyingFrames;
        DEAD_FRAMES = deadFrames;
        JUMPING_FRAMES = jumpingFrames;
    }

    public Sprite(String path, int spriteWidth, int spriteHeight) {
        spriteSheet = Texture.loadTexture(path);

        SPRITE_WIDTH = spriteWidth;
        SPRITE_HEIGHT = spriteHeight;

        TILES_IN_TILESET_X_AXIS = spriteSheet.getWidth() / SPRITE_WIDTH;
        TILES_IN_TILESET_Y_AXIS = spriteSheet.getHeight() / SPRITE_HEIGHT;
    }

    public Texture getSpriteSheet() {
        return spriteSheet;
    }

    public void draw(int x, int y, int spriteCoordinateFromSpriteSheetX, int spriteCoordinateFromSpriteSheetY, double transparency) {
        draw(x, y, spriteCoordinateFromSpriteSheetX, spriteCoordinateFromSpriteSheetY, transparency, Camera.getZoom());
    }

    public void draw(int x, int y, int spriteCoordinateFromSpriteSheetX, int spriteCoordinateFromSpriteSheetY, double transparency, double scale) {
        spriteSheet.bind();
        glEnable(GL_TEXTURE_2D);

        float width = (float) spriteSheet.getWidth() / (float) SPRITE_WIDTH;
        float height = (float) spriteSheet.getHeight() / (float) SPRITE_HEIGHT;

        double u = ((1.0 / width) * spriteCoordinateFromSpriteSheetX);
        double v = 1.0 - ((1.0 / height) * spriteCoordinateFromSpriteSheetY);
        double u2 = u + (1.0 / width);
        double v2 = v - (1.0 / height);

        OpenGLManager.glBegin(GL_QUADS);
        y -= (int) (SPRITE_HEIGHT * scale);
        OpenGLManager.drawTexture(x, y, u, v, u2, v2, (int) (SPRITE_WIDTH * scale), (int) (SPRITE_HEIGHT * scale), transparency, 1f, 1f, 1f);
        glEnd();
    }
}
