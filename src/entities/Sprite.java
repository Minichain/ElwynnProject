package entities;

import main.OpenGLManager;
import main.Texture;
import org.lwjgl.opengl.ARBShaderObjects;
import scene.Camera;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

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

    public void draw(int x, int y, int spriteCoordinateFromSpriteSheetX, int spriteCoordinateFromSpriteSheetY, float transparency) {
        draw(x, y, spriteCoordinateFromSpriteSheetX, spriteCoordinateFromSpriteSheetY, transparency, Camera.getZoom());
    }

    public void draw(int x, int y, int spriteCoordinateFromSpriteSheetX, int spriteCoordinateFromSpriteSheetY, float transparency, double scale) {
        glActiveTexture(GL_TEXTURE0);
        spriteSheet.bind();

        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        float width = (float) spriteSheet.getWidth() / (float) SPRITE_WIDTH;
        float height = (float) spriteSheet.getHeight() / (float) SPRITE_HEIGHT;

        float u = ((1f / width) * spriteCoordinateFromSpriteSheetX);
        float v = 1f - ((1f / height) * spriteCoordinateFromSpriteSheetY);
        float u2 = u + (1f / width);
        float v2 = v - (1f / height);

        OpenGLManager.glBegin(GL_QUADS);

        y -= (int) (SPRITE_HEIGHT * scale);
        OpenGLManager.drawTexture(x, y, u, v, u2, v2, (int) (SPRITE_WIDTH * scale), (int) (SPRITE_HEIGHT * scale), transparency, 1f, 1f, 1f);

        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);

        glEnd();
    }
}
