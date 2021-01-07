package main;

import entities.MusicalMode;
import entities.Player;
import listeners.InputListenerManager;
import utils.MathUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;

public class MusicalModeSelector {
    private static MusicalModeSelector instance = null;
    private boolean showing;
    private int selectedMusicalMode;
    private Coordinates[][] vertices;

    public MusicalModeSelector() {
        showing = false;
    }

    public static MusicalModeSelector getInstance() {
        if (instance == null) {
            instance = new MusicalModeSelector();
        }
        return instance;
    }

    public boolean isShowing() {
        return showing;
    }

    public void setShowing(boolean showing) {
        if (!showing) Player.getInstance().setMusicalMode(selectedMusicalMode);
        GameTime.setSlowMotion(showing);
        this.showing = showing;
    }

    public void update(long timeElapsed) {
        selectedMusicalMode = Player.getInstance().getMusicalMode().value;
        int sections = MusicalMode.values().length;
        vertices = new Coordinates[3][sections];
        double angle = - Math.PI / 2;
        int sectionLength = (int) (500 * Parameters.getResolutionFactor());

        for (int i = 0; i < sections; i++) {
            vertices[0][i] = new Coordinates(Window.getWidth() / 2, Window.getHeight() / 2);
            vertices[1][i] = new Coordinates(vertices[0][i].x + (int) (sectionLength * Math.cos(angle)), vertices[0][i].y + (int) (sectionLength * Math.sin(angle)));
            angle += 2 * Math.PI / sections;
            vertices[2][i] = new Coordinates(vertices[0][i].x + (int) (sectionLength * Math.cos(angle)), vertices[0][i].y + (int) (sectionLength * Math.sin(angle)));

            if (MathUtils.isPointInsideTriangle(InputListenerManager.getMouseWindowCoordinates(), vertices[0][i], vertices[1][i], vertices[2][i])) {
                selectedMusicalMode = i;
            }
        }
    }

    public void render() {
        double[] vertex1, vertex2, vertex3;
        float r, g, b;
        double transparency;
        int sections = MusicalMode.values().length;

        /** RENDER TRIANGLES **/
        glDisable(GL_TEXTURE_2D);
        OpenGLManager.glBegin(GL_TRIANGLES);
        for (int i = 0; i < sections; i++) {
            vertex1 = new double[]{vertices[0][i].x, vertices[0][i].y};
            vertex3 = new double[]{vertices[1][i].x, vertices[1][i].y};
            vertex2 = new double[]{vertices[2][i].x, vertices[2][i].y};
            if (selectedMusicalMode == i) transparency = 0.75;
            else transparency = 0.25;
            r =  MusicalMode.values()[i].getColor().getRed() / 255f;
            g =  MusicalMode.values()[i].getColor().getGreen() / 255f;
            b =  MusicalMode.values()[i].getColor().getBlue() / 255f;
            glColor4f(r, g, b, (float) transparency);
            glVertex2d(vertex1[0], vertex1[1]);
            glColor4f(r, g, b, (float) 0);
            glVertex2d(vertex2[0], vertex2[1]);
            glColor4f(r, g, b, (float) 0);
            glVertex2d(vertex3[0], vertex3[1]);
        }
        glEnd();

        /** RENDER ICONS **/
        int sectionLength = (int) (500 * Parameters.getResolutionFactor());
        double angle = - Math.PI / 2 + (Math.PI / (sections));
        int x, y;
        for (int i = 0; i < sections; i++) {
            x = (int) (vertices[0][i].x + (sectionLength / 2) * Math.cos(angle));
            y = (int) (vertices[0][i].y + (sectionLength / 2) * Math.sin(angle));
            if (selectedMusicalMode == i) transparency = 1.0;
            else transparency = 0.5;
            MusicalMode.values()[i].getSprite().draw(x, y, (float) transparency,
                    4f * Parameters.getResolutionFactor(), MusicalMode.values()[i].getColor(), true);
            angle += 2 * Math.PI / sections;
        }
    }
}
