package entities;

import main.Coordinates;
import main.OpenGLManager;
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
        getSprite().draw(x, y, (int) getSpriteCoordinateFromSpriteSheetX(), (int) getSpriteCoordinateFromSpriteSheetY(), 1f);
    }

    public void drawHitBox(int x, int y) {
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);

        OpenGLManager.glBegin(GL_LINES);
        glColor4f(1f, 1f, 1f, 1f);

        int width = getSprite().SPRITE_WIDTH;
        int height = getSprite().SPRITE_HEIGHT;

        //1st Line
        glVertex2i(x, y);
        glVertex2i((int) (x + width * Camera.getZoom()), y);

        //2nd Line
        glVertex2i((int) (x + width * Camera.getZoom()), y);
        glVertex2i((int) (x + width * Camera.getZoom()), (int) (y - height * Camera.getZoom()));

        //3rd Line
        glVertex2i((int) (x + width * Camera.getZoom()), (int) (y - height * Camera.getZoom()));
        glVertex2i(x, (int) (y - height * Camera.getZoom()));

        //4th Line
        glVertex2i(x, (int) (y - height * Camera.getZoom()));
        glVertex2i(x, y);

        glEnd();

        getCollision().draw();

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
    }
}