package board;

import entities.*;
import main.*;
import main.Window;
import particles.Particle;
import particles.ParticleManager;
import scene.Scene;
import utils.MathUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

import static org.lwjgl.opengl.GL11.*;

public class FretBoard {
    private static FretBoard instance = null;
    private Coordinates coordinates;
    private int newNotePeriod = 500;
    private int newNoteCoolDown;
    private ArrayList<FretBoardNote> notes;
    private Coordinates[] targetNotes;
    private boolean[] fretsPressed = {false, false, false, false};
    private boolean playingMusic;
    private float transparency;

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
        transparency = 0f;
    }

    public void update(long timeElapsed) {
        for (int i = 0; i < notes.size(); i++) {
            notes.get(i).update(timeElapsed);
        }

        if (playingMusic) {
            if (transparency < 1f) transparency += timeElapsed * 0.001f;
        } else if (transparency > 0f) {
            transparency -= timeElapsed * 0.001f;
            return;
        }

        if (newNoteCoolDown <= 0) {
            int r = (int) (MathUtils.random(0, 4) % 4.0);
            notes.add(new FretBoardNote(r));
            newNoteCoolDown = newNotePeriod;
        } else {
            newNoteCoolDown -= timeElapsed;
        }
    }

    public void render() {
        if (transparency <= 0f) return;

        SpriteManager.getInstance().FRET_BOARD.getSpriteSheet().bind();
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        OpenGLManager.glBegin(GL_QUADS);

        Iterator<FretBoardNote> iterator = notes.iterator();
        while (iterator.hasNext()) {
            FretBoardNote note = iterator.next();
            if (note.isDead()) {
                iterator.remove();
            } else {
                note.render(transparency);
            }
        }

        for (int i = 0; i < targetNotes.length; i++) {
            int spriteCoordinateFromSpriteSheetY = 0;
            if (fretsPressed[i]) spriteCoordinateFromSpriteSheetY = 1;
            SpriteManager.getInstance().FRET_BOARD.draw((int) targetNotes[i].x, (int) targetNotes[i].y, 0, spriteCoordinateFromSpriteSheetY,
                    transparency, 4f * Parameters.getHeightResolutionFactor(), Player.getInstance().getMusicalMode().getColor(), true, true);
        }

        glEnd();
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
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

    public Coordinates[] getTargetNotes() {
        return targetNotes;
    }

    public void setFretPressed(int fret, boolean pressed) {
        fretsPressed[fret] = pressed;
    }

    public void setPlayingMusic(boolean playingMusic) {
        this.playingMusic = playingMusic;
    }

    public void playNote() {
        Iterator<FretBoardNote> iterator = notes.iterator();
        while (iterator.hasNext()) {
            FretBoardNote note = iterator.next();
            if (Math.abs(note.getPathTraveled() - 1.0) < 0.1 && fretsPressed[note.getTargetNote()]) {
                iterator.remove();
                MusicalMode musicalMode = Player.getInstance().getMusicalMode();
                MusicalNote musicalNote = musicalMode.getRandomNote(MusicalNote.A);
                musicalNote.play();

                //Generate particles with the colour of the musical mode
                Coordinates particleCoordinates;
                double randomAngle;
                double[] generationVector;
                Particle particle;
                double[] velocityVector;
                int numberOfParticles = 4;
                for (int i = 0; i < numberOfParticles; i++) {
                    randomAngle = MathUtils.random(0, 2.0 * Math.PI);
                    generationVector = new double[]{15 * Math.random(), 0};
                    generationVector = MathUtils.rotateVector(generationVector, randomAngle);
                    velocityVector = new double[]{0, -0.1};
                    particleCoordinates = new Coordinates(targetNotes[note.getTargetNote()].x + generationVector[0], targetNotes[note.getTargetNote()].y + generationVector[1]);
                    particleCoordinates = particleCoordinates.toWorldCoordinates();
                    particle = new Particle(particleCoordinates, velocityVector, 0.25, 1.5f, musicalMode.getColor(), true);
                    ParticleManager.getInstance().addParticle(particle);
                }

                Scene.getInstance().getListOfShockWaves().add(new ShockWave(Player.getInstance().getCenterOfMassWorldCoordinates(), musicalMode, musicalNote));
            }
        }
    }
}
