package entities;

import main.Coordinates;
import main.OpenGLManager;
import main.TextRendering;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class FloatingText {
    private static ArrayList<FloatingTextEntity> listOfFloatingTextEntities = new ArrayList<>();

    public FloatingText() {
    }

    public static void addTextToList(FloatingTextEntity floatingTextEntity) {
        listOfFloatingTextEntities.add(floatingTextEntity);
    }

    public static void renderAndUpdate(long timeElapsed) {
        FloatingTextEntity entity;
        TextRendering.fontSpriteWhite.bind();
        glEnable(GL_TEXTURE_2D);
        OpenGLManager.glBegin(GL_QUADS);
        for (int i = 0; i < listOfFloatingTextEntities.size(); i++) {
            entity = listOfFloatingTextEntities.get(i);
            double alpha = 1.0 - entity.timeLiving / entity.timeToLive;
            Coordinates entityCameraCoordinates = entity.coordinates.toCameraCoordinates();
            if (entity.isDangerText()) {
                TextRendering.renderText((int) entityCameraCoordinates.x, (int) entityCameraCoordinates.y, entity.text, 2, true, alpha, 1f, 0f, 0f);
            } else {
                TextRendering.renderText((int) entityCameraCoordinates.x, (int) entityCameraCoordinates.y, entity.text, 2, true, alpha);
            }
            if (entity.timeLiving < entity.timeToLive) {
                entity.timeLiving += timeElapsed;
                entity.coordinates = new Coordinates(entity.coordinates.x, entity.coordinates.y - entity.movingSpeed);
            } else {
                listOfFloatingTextEntities.remove(entity);
            }
        }
        glEnd();
    }
}
