package board;

import audio.OpenALManager;
import entities.*;
import main.*;
import main.Window;
import particles.Particle;
import particles.ParticleManager;
import scene.Camera;
import scene.Scene;
import text.TextRendering;
import utils.MathUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

import static org.lwjgl.opengl.GL11.*;

public class FretBoard {
    private static FretBoard instance = null;
    private Coordinates coordinates;
    private ArrayList<FretBoardNote> notes;
    private Coordinates[] targetNotes;
    private boolean[] fretsPressed = {false, false, false, false};
    private boolean playingMusic;
    private float transparency;
    private int combo;
    private float comboTextScale;

    /** MUSIC GENERATOR **/
    private final float beatPeriod = 1000f;
    private float beatProgress;
    private float timeElapsed;

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
        combo = 1;
        comboTextScale = 0f;
    }

    private void setupTempos() {
        this.beatProgress = 0;
        this.timeElapsed = 0;
        this.combo = 0;
    }

    public void update(long timeElapsed) {
        for (int i = 0; i < notes.size(); i++) {
            notes.get(i).update(timeElapsed);
        }

        if (playingMusic) {
            if (transparency < 1f) transparency += timeElapsed * 0.001f;
            OpenALManager.onMusicLevelChange(Parameters.getMusicSoundLevel() * (1f - transparency));
        } else if (transparency > 0f) {
            transparency -= timeElapsed * 0.001f;
            OpenALManager.onMusicLevelChange(Parameters.getMusicSoundLevel() * (1f - transparency));
            return;
        }

        if (!Player.getInstance().isPlayingMusic()) return;

        float halfFramePeriod = (1000f / (FramesPerSecond.getFramesPerSecond())) / 2f - 0.1f;
        halfFramePeriod = halfFramePeriod / GameTime.getTimeSpeedFactor();
//        Log.l("halfFramePeriod: " + halfFramePeriod);
//        Log.l("beatProgress: " + beatProgress);
        if (this.timeElapsed > beatPeriod * 2) {
            if (beatProgress < halfFramePeriod || (beatPeriod - halfFramePeriod) < beatProgress
                    || (Math.random() < 0.25 && Math.abs(beatProgress - beatPeriod * 1f / 4f) < halfFramePeriod)) {
                OpenALManager.playSound(OpenALManager.SOUND_KICK_01);
            }
        }

        if (this.timeElapsed > beatPeriod) {
            if (beatProgress < halfFramePeriod || (beatPeriod - halfFramePeriod) < beatProgress
                    || Math.abs(beatProgress - (beatPeriod * 1f / 4f)) < halfFramePeriod
                    || Math.abs(beatProgress - (beatPeriod * 2f / 4f)) < halfFramePeriod
                    || Math.abs(beatProgress - (beatPeriod * 3f / 4f)) < halfFramePeriod) {
                OpenALManager.playSound(OpenALManager.SOUND_HI_HAT_01);
            }
        }

        if (this.timeElapsed > beatPeriod * 2.5f) {
            if (Math.abs(beatProgress - beatPeriod / 2f) < halfFramePeriod
                    || (Math.random() < 0.25 && Math.abs(beatProgress - beatPeriod * 3f / 4f) < halfFramePeriod)) {
                OpenALManager.playSound(OpenALManager.SOUND_SNARE_01);
            }
        }

        if (this.timeElapsed > beatPeriod) {
            double probability = Player.getInstance().isHasteEffect() ? 0.5 : 0.25;
            if ((Math.random() < probability && (beatProgress < halfFramePeriod || Math.abs(beatProgress - beatPeriod) < halfFramePeriod))
                    || (Math.random() < probability && Math.abs(beatProgress - (beatPeriod * 1f / 4f)) < halfFramePeriod)
                    || (Math.random() < probability && Math.abs(beatProgress - (beatPeriod * 2f / 4f)) < halfFramePeriod)
                    || (Math.random() < probability && Math.abs(beatProgress - (beatPeriod * 3f / 4f)) < halfFramePeriod)) {
                int r = (int) (MathUtils.random(0, 4) % 4.0);
                notes.add(new FretBoardNote(r));
            }
        }

        beatProgress = (beatProgress + (int) timeElapsed) % beatPeriod;
        this.comboTextScale += timeElapsed * 0.01f;
        if (this.comboTextScale > 10f) this.comboTextScale = 10f;
        this.timeElapsed += timeElapsed;
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

        if (combo > 5) {
            Coordinates coordinates = new Coordinates(targetNotes[3].x, targetNotes[3].y);
            String textToRender = "x" + combo;
            coordinates.x += 25 * Parameters.getWidthResolutionFactor();
            coordinates.y -= 50 * Parameters.getHeightResolutionFactor();
            Color color = new Color(1f, 1f, 1f);
            float scale = 2f;
            if (combo >= 10) {
                color = new Color(1f, 0.5f, 0.5f);
                scale = 4f;
            }
            scale = (float) Math.log(scale * this.comboTextScale) + 1;
            coordinates.x += scale;
            coordinates.y += scale;
            TextRendering.renderText((float) coordinates.x, (float) coordinates.y, textToRender, scale,
                    false, transparency, color.getRed(), color.getGreen(), color.getBlue());
        }
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
        if (playingMusic) setupTempos();
    }

    public void playNote() {
        Iterator<FretBoardNote> iterator = notes.iterator();
        while (iterator.hasNext()) {
            FretBoardNote note = iterator.next();
            if (Math.abs(note.getPathTraveled() - 1.0) < 0.1 && fretsPressed[note.getTargetNote()]) {
                // You hit the note at the right time!
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

                combo++;
                comboTextScale = 0f;
                Scene.getInstance().getListOfShockWaves().add(new ShockWave(Player.getInstance().getCenterOfMassWorldCoordinates(),
                        Player.getInstance().facingVector, musicalMode, musicalNote, 250f * combo));
                return;
            }
        }

        // You didn't hit the note at the right time :(
        combo = 0;
        if (Math.random() < 0.5) {
            OpenALManager.playSound(OpenALManager.SOUND_MISS_NOTE_01);
        } else {
            OpenALManager.playSound(OpenALManager.SOUND_MISS_NOTE_02);
        }
        Camera.getInstance().shake(100, 1f);
    }

    public float getTransparency() {
        return transparency;
    }
}
