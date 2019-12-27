package main;

import static org.lwjgl.opengl.GL11.*;

public class MyOpenGL {
    public static void prepareOpenGL() {
        //usually glOrtho would not be included in our game loop
        //however, since it's deprecated, let's keep it inside of this debug function which we will remove later
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, Parameters.getInstance().getWindowWidth(), Parameters.getInstance().getWindowHeight(), 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glEnable(GL_TEXTURE_2D); //likely redundant; will be removed upon migration to "modern GL"
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
    }

    public static void drawTexture(int x, int y, float u, float v, float u2, float v2, int spriteWidth, float spriteHeight) {
        glTexCoord2f(u, v);
        glVertex2f(x, y);
        glTexCoord2f(u, v2);
        glVertex2f(x, y + spriteHeight);
        glTexCoord2f(u2, v2);
        glVertex2f(x + spriteWidth, y + spriteHeight);
        glTexCoord2f(u2, v);
        glVertex2f(x + spriteWidth, y);
    }
}
