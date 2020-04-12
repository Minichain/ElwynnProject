package entities;

import main.Coordinates;
import main.OpenGLManager;
import main.Parameters;
import scene.Camera;

import static org.lwjgl.opengl.GL11.*;

public abstract class StaticGraphicEntity extends GraphicEntity {
    private Coordinates tileCoordinates;
    private Collision collision;

    public StaticGraphicEntity(int x, int y) {
        super(x, y);
        tileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(x, y);
    }

    public Coordinates getTileCoordinates() {
        return tileCoordinates;
    }

    public void setCollision(Collision collision) {
        this.collision = collision;
    }

    public Collision getCollision() {
        return collision;
    }

    public void drawSprite(int x, int y) {
        getSprite().draw(x, y, (int) getSpriteCoordinateFromSpriteSheetX(), (int) getSpriteCoordinateFromSpriteSheetY(), 1.0);

        if (Parameters.isDebugMode()) {
            glDisable(GL_TEXTURE_2D);

            OpenGLManager.glBegin(GL_LINES);

            //1st Line
            glVertex2i(x, y);
            glVertex2i((int) (x + getSpriteSheet().getWidth() * Camera.getZoom()), y);

            //2nd Line
            glVertex2i((int) (x + getSpriteSheet().getWidth() * Camera.getZoom()), y);
            glVertex2i((int) (x + getSpriteSheet().getWidth() * Camera.getZoom()), (int) (y - getSpriteSheet().getHeight() * Camera.getZoom()));

            //3rd Line
            glVertex2i((int) (x + getSpriteSheet().getWidth() * Camera.getZoom()), (int) (y - getSpriteSheet().getHeight() * Camera.getZoom()));
            glVertex2i(x, (int) (y - getSpriteSheet().getHeight() * Camera.getZoom()));

            //4th Line
            glVertex2i(x, (int) (y - getSpriteSheet().getHeight() * Camera.getZoom()));
            glVertex2i(x, y);

            glEnd();

            getCollision().draw();
        }
    }
}