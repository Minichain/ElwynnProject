package entities;

import main.OpenGLManager;
import main.Texture;
import scene.Camera;

import java.awt.*;

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
    public int ROLLING_FRAMES;
    public int ATTACKING_FRAMES;

    public int TILES_IN_TILESET_X_AXIS;
    public int TILES_IN_TILESET_Y_AXIS;

    public Sprite(String path, int spriteWidth, int spriteHeight, int idleFrames) {
        this(path, spriteWidth, spriteHeight, idleFrames, -1, -1, -1, -1);
    }

    public Sprite(String path, int spriteWidth, int spriteHeight, int idleFrames, int runningFrames, int dyingFrames, int deadFrames, int rollingFrame) {
        this(path, spriteWidth, spriteHeight, idleFrames, runningFrames, dyingFrames, deadFrames, rollingFrame, -1);
    }

    public Sprite(String path, int spriteWidth, int spriteHeight, int idleFrames, int runningFrames, int dyingFrames, int deadFrames, int rollingFrame, int attackingFrames) {
        spriteSheet = Texture.loadTexture(path);

        SPRITE_WIDTH = spriteWidth;
        SPRITE_HEIGHT = spriteHeight;

        IDLE_FRAMES = idleFrames;
        RUNNING_FRAMES = runningFrames;
        DYING_FRAMES = dyingFrames;
        DEAD_FRAMES = deadFrames;
        ROLLING_FRAMES = rollingFrame;
        ATTACKING_FRAMES = attackingFrames;
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

    public void draw(int x, int y, float transparency) {
        draw(x, y, 0, 0, transparency, Camera.getZoom());
    }

    public void draw(int x, int y, float transparency, double scale, Color color) {
        draw(x, y, 0, 0, transparency, scale, color, false);
    }

    public void draw(int x, int y, float transparency, double scale, Color color, boolean centered) {
        draw(x, y, 0, 0, transparency, scale, color, centered);
    }

    public void draw(int x, int y, int spriteCoordinateFromSpriteSheetX, int spriteCoordinateFromSpriteSheetY, float transparency) {
        draw(x, y, spriteCoordinateFromSpriteSheetX, spriteCoordinateFromSpriteSheetY, transparency, Camera.getZoom());
    }

    public void draw(int x, int y, int spriteCoordinateFromSpriteSheetX, int spriteCoordinateFromSpriteSheetY, float transparency, double scale) {
        draw(x, y, spriteCoordinateFromSpriteSheetX, spriteCoordinateFromSpriteSheetY, transparency, scale, new Color(1f, 1f, 1f), false);
    }

    public void draw(int x, int y, int spriteCoordinateFromSpriteSheetX, int spriteCoordinateFromSpriteSheetY, float transparency, double scale, boolean centered) {
        draw(x, y, spriteCoordinateFromSpriteSheetX, spriteCoordinateFromSpriteSheetY, transparency, scale, new Color(1f, 1f, 1f), centered);
    }

    public void draw(int x, int y, int spriteCoordinateFromSpriteSheetX, int spriteCoordinateFromSpriteSheetY,
                     float transparency, double scale, Color color, boolean centered) {
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

        if (centered) {
            y -= (int) (SPRITE_HEIGHT * scale / 2.0);
            x -= (int) (SPRITE_WIDTH * scale / 2.0);
        } else {
            y -= (int) (SPRITE_HEIGHT * scale);
        }

        OpenGLManager.drawTexture(x, y, u, v, u2, v2, (int) (SPRITE_WIDTH * scale), (int) (SPRITE_HEIGHT * scale), transparency,
                color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);

        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);

        glEnd();
    }

    public void customDraw(int x, int y, int width, int height, float u, float v, float u2, float v2, float transparency, double scale) {
        customDraw(x, y, width, height, u, v, u2, v2, transparency, scale, new Color(255, 255, 255), false);
    }

    public void customDraw(int x, int y, int width, int height, float u, float v, float u2, float v2,
                     float transparency, double scale, boolean centered) {
        customDraw(x, y, width, height, u, v, u2, v2, transparency, scale, new Color(255, 255, 255), centered);
    }

    public void customDraw(int x, int y, int width, int height, float u, float v, float u2, float v2,
                     float transparency, double scale, Color color, boolean centered) {
        glActiveTexture(GL_TEXTURE0);
        spriteSheet.bind();

        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        OpenGLManager.glBegin(GL_QUADS);

        if (centered) {
            y -= (int) (SPRITE_HEIGHT * scale / 2.0);
            x -= (int) (SPRITE_WIDTH * scale / 2.0);
        } else {
            y -= (int) (SPRITE_HEIGHT * scale);
        }

        OpenGLManager.drawTexture(x, y, u, v, u2, v2, width, height, transparency,
                color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);

        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);

        glEnd();
    }
}
