package board;

import main.Coordinates;
import main.OpenGLManager;
import main.Parameters;
import main.Window;
import utils.MathUtils;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class FretBoard {
    private static FretBoard instance = null;
    private Coordinates coordinates;
    private int newNotePeriod = 250;
    private int newNoteCoolDown;
    private ArrayList<FretBoardNote> notes;
    private Coordinates[] targetNotes;

    private FretBoard() {
        init();
    }

    public static FretBoard getInstance() {
        if (instance == null) {
            instance = new FretBoard();
        }
        return instance;
    }

    private void init() {
        coordinates = new Coordinates(Window.getWidth() / 2.0, Window.getHeight() / 2.0);
        notes = new ArrayList<>();
        targetNotes = new Coordinates[4];
        setupCoordinates();
    }

    public void update(long timeElapsed) {
        for (int i = 0; i < notes.size(); i++) {
            notes.get(i).update(timeElapsed);
        }

        if (newNoteCoolDown <= 0) {
            int r = (int) (MathUtils.random(0, 4) % 4.0);
            notes.add(new FretBoardNote(targetNotes[r].x, targetNotes[r].y - 250));
            newNoteCoolDown = newNotePeriod;
        } else {
            newNoteCoolDown -= timeElapsed;
        }
    }

    public void render() {
        for (int i = 0; i < notes.size(); i++) {
            notes.get(i).render();
        }

        glDisable(GL_TEXTURE_2D);
        OpenGLManager.glBegin(GL_TRIANGLES);
        for (int i = 0; i < targetNotes.length; i++) {
            OpenGLManager.drawRectangle((int) targetNotes[i].x, (int) targetNotes[i].y, 25, 25);
        }
        glEnd();
    }

    public void onResolutionChanged() {
        setupCoordinates();
    }

    private void setupCoordinates() {
        coordinates.x = Window.getWidth() / 2.0;
        coordinates.y = Window.getHeight() / 2.0 + 360f * Parameters.getHeightResolutionFactor();

        double x;
        double y = coordinates.y;
        for (int i = 0; i < 4; i++) {
            x = coordinates.x + (i * 100 - 150) * Parameters.getWidthResolutionFactor();
            targetNotes[i] = new Coordinates(x, y);
        }
    }
}
