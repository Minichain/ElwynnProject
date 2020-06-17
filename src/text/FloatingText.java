package text;

import main.GameStatus;
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

    public static void updateAndRender(long timeElapsed) {
        FloatingTextEntity entity;
        TextRendering.fontSpriteWhite.bind();
        glEnable(GL_TEXTURE_2D);
        OpenGLManager.glBegin(GL_QUADS);
        for (int i = 0; i < listOfFloatingTextEntities.size(); i++) {
            entity = listOfFloatingTextEntities.get(i);
            if (GameStatus.getStatus() == GameStatus.Status.RUNNING) {
                entity.update(timeElapsed);
            }
            entity.render();
        }
        glEnd();
        glDisable(GL_TEXTURE_2D);
    }

    public static ArrayList<FloatingTextEntity> getListOfFloatingTextEntities() {
        return listOfFloatingTextEntities;
    }
}
