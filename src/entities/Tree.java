package entities;

import main.Coordinates;
import main.OpenGLManager;
import main.Parameters;
import main.Texture;
import scene.Camera;
import scene.Scene;

import static org.lwjgl.opengl.GL11.*;

public class Tree extends StaticGraphicEntity {
    public static int ENTITY_CODE = 1;

    public Tree(int x, int y) {
        super(x, y);
        init();
    }

    private void init() {
        setWorldCoordinates(Coordinates.tileCoordinatesToWorldCoordinates((int) getTileCoordinates().x, (int) getTileCoordinates().y));
        setSprite(SpriteManager.getInstance().TREE);
        setCollision(new Collision(new Coordinates(getWorldCoordinates().x + 16, getWorldCoordinates().y - 8), 32, 16));   //Square collision
//        setCollision(new Collision(new Coordinates(getWorldCoordinates().x + 16, getWorldCoordinates().y - 6), 14));   //Circle collision
        Scene.getInstance().getListOfEntities().add(this);
        Scene.getInstance().getListOfStaticEntities().add(this);
    }

    @Override
    public void update(long timeElapsed) {

    }

    @Override
    public Texture getSpriteSheet() {
        return getSprite().getSpriteSheet();
    }

    @Override
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

    @Override
    public int getEntityCode() {
        return ENTITY_CODE;
    }
}
