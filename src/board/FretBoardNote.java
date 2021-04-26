package board;

import main.Coordinates;
import main.OpenGLManager;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;

public class FretBoardNote {
    private Coordinates coordinates;

    public FretBoardNote(double x, double y) {
        coordinates = new Coordinates(x, y);
    }

    public void update(long timeElapsed) {
        coordinates.y += timeElapsed * 0.1;
    }

    public void render() {
        glDisable(GL_TEXTURE_2D);
        OpenGLManager.glBegin(GL_TRIANGLES);
        OpenGLManager.drawRectangle((int) (coordinates.x), (int) (coordinates.y), 25, 25);
        glEnd();
    }
}
