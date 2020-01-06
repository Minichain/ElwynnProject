package main;

import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.*;

public class MyOpenGL {
    public static void prepareOpenGL() {
        GL.createCapabilities();
        glEnable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
    }

    public static void prepareFrame() {
        glClear(GL_COLOR_BUFFER_BIT);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, Parameters.getInstance().getWindowWidth(), Parameters.getInstance().getWindowHeight(), 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }

    public static void drawTexture(int x, int y, float u, float v, float u2, float v2, float spriteWidth, float spriteHeight) {
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glTexCoord2f(u, v);
        glVertex2f(x, y);
        glTexCoord2f(u, v2);
        glVertex2f(x, y + spriteHeight);
        glTexCoord2f(u2, v2);
        glVertex2f(x + spriteWidth, y + spriteHeight);
        glTexCoord2f(u2, v);
        glVertex2f(x + spriteWidth, y);
    }

    public static void drawTexture(int x, int y, double u, double v, double u2, double v2, float spriteWidth, float spriteHeight) {
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glTexCoord2d(u, v);
        glVertex2f(x, y);
        glTexCoord2d(u, v2);
        glVertex2f(x, y + spriteHeight);
        glTexCoord2d(u2, v2);
        glVertex2f(x + spriteWidth, y + spriteHeight);
        glTexCoord2d(u2, v);
        glVertex2f(x + spriteWidth, y);
    }

    public static void drawTextureAlpha(int x, int y, double u, double v, double u2, double v2, float spriteWidth, float spriteHeight, double transparency) {
        glColor4f(1.0f, 1.0f, 1.0f, (float) transparency);
        glTexCoord2d(u, v);
        glVertex2f(x, y);
        glTexCoord2d(u, v2);
        glVertex2f(x, y + spriteHeight);
        glTexCoord2d(u2, v2);
        glVertex2f(x + spriteWidth, y + spriteHeight);
        glTexCoord2d(u2, v);
        glVertex2f(x + spriteWidth, y);
    }
}
