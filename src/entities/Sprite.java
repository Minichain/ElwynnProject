package entities;

import main.OpenGLManager;
import main.Texture;
import scene.Camera;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glEnd;

public class Sprite {
    private Texture spriteSheet;

    public int SPRITE_WIDTH;
    public int SPRITE_HEIGHT;

    public int IDLE_FRAMES;
    public int RUNNING_FRAMES;
    public int DYING_FRAMES;
    public int DEAD_FRAMES;

    public Sprite(String path, int spriteWidth, int spriteHeight, int idleFrames, int runningFrames, int dyingFrames, int deadFrames) {
        spriteSheet = Texture.loadTexture(path);

        SPRITE_WIDTH = spriteWidth;
        SPRITE_HEIGHT = spriteHeight;

        IDLE_FRAMES = idleFrames;
        RUNNING_FRAMES = runningFrames;
        DYING_FRAMES = dyingFrames;
        DEAD_FRAMES = deadFrames;
    }

    public Texture getSpriteSheet() {
        return spriteSheet;
    }

    public void draw(int x, int y, int spriteCoordinateFromSpriteSheetX, int spriteCoordinateFromSpriteSheetY) {
        draw(x, y, spriteCoordinateFromSpriteSheetX, spriteCoordinateFromSpriteSheetY, false);
    }

    public void draw(int x, int y, int spriteCoordinateFromSpriteSheetX, int spriteCoordinateFromSpriteSheetY, boolean centered) {
        spriteSheet.bind();

        float width = (float) spriteSheet.getWidth() / (float) SPRITE_WIDTH;
        float height = (float) spriteSheet.getHeight() / (float) SPRITE_HEIGHT;

        float u = ((1f / width) * spriteCoordinateFromSpriteSheetX);
        float v = 1f - ((1f / height) * spriteCoordinateFromSpriteSheetY);
        float u2 = u + (1f / width);
        float v2 = v - (1f / height);
        double scale = Camera.getZoom();

        OpenGLManager.glBegin(GL_QUADS);
        if (centered) {
            x -= (int) ((SPRITE_WIDTH / 2) * scale);
            y -= (int) ((SPRITE_HEIGHT / 2) * scale);
        }
        OpenGLManager.drawTexture(x, y, u, v, u2, v2, (float) (SPRITE_WIDTH * scale), (float) (SPRITE_HEIGHT * scale));
        glEnd();
    }
}
