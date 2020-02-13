package main;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class FloatingText {
    private static ArrayList<FloatingTextEntity> listOfFloatingTextEntities = new ArrayList<>();

    public FloatingText() {
    }

    public static void addTextToList(FloatingTextEntity floatingTextEntity) {
        listOfFloatingTextEntities.add(floatingTextEntity);
    }

    public static void renderAndUpdate() {
        FloatingTextEntity entity;
        TextRendering.fontSpriteWhite.bind();
        glBegin(GL_QUADS);
        for (int i = 0; i < listOfFloatingTextEntities.size(); i++) {
            entity = listOfFloatingTextEntities.get(i);
            double alpha = 1.0 - entity.timeLiving / entity.timeToLive;
            if (entity.isDangerText()) {
                TextRendering.renderText((int) entity.coordinates.x, (int) entity.coordinates.y, entity.text, 2, true, alpha, 1f, 0f, 0f);
            } else {
                TextRendering.renderText((int) entity.coordinates.x, (int) entity.coordinates.y, entity.text, 2, true, alpha);
            }
            if (entity.timeLiving < entity.timeToLive) {
                entity.timeLiving += 1;
                entity.coordinates = new Coordinates(entity.coordinates.x, entity.coordinates.y - entity.movingSpeed);
            } else {
                listOfFloatingTextEntities.remove(entity);
            }
        }
        glEnd();
    }
}
