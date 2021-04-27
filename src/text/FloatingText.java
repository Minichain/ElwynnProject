package text;

import main.GameStatus;
import main.Log;
import main.OpenGLManager;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class FloatingText {
    private static ArrayList<FloatingTextEntity> listOfFloatingTextEntities = new ArrayList<>();

    public FloatingText() {
    }

    public static void addTextToList(FloatingTextEntity floatingTextEntity) {
        listOfFloatingTextEntities.add(floatingTextEntity);
    }

    public static void update(long timeElapsed) {
        ArrayList<FloatingTextEntity> tempList = new ArrayList<>(listOfFloatingTextEntities);
        for (FloatingTextEntity entity : tempList) {
            if (GameStatus.getStatus() == GameStatus.Status.RUNNING) {
                entity.update(timeElapsed);
            }
        }
    }

    public static void render() {
        TextRendering.fontSpriteWhite.bind();
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        OpenGLManager.glBegin(GL_QUADS);
        for (FloatingTextEntity entity : listOfFloatingTextEntities) {
            entity.render();
        }
        glEnd();
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }

    public static ArrayList<FloatingTextEntity> getListOfFloatingTextEntities() {
        return listOfFloatingTextEntities;
    }
}
