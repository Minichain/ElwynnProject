package entities;

import utils.MathUtils;

import java.awt.*;

public enum MusicalMode {
    IONIAN(0),     //MAJOR SCALE
    DORIAN(1),     //Minor
    PHRYGIAN(2),   //Minor
    LYDIAN(3),     //Major
    MIXOLYDIAN(4), //Major
    AEOLIAN(5),    //NATURAL MINOR SCALE
    LOCRIAN(6);

    public int value;

    MusicalMode(final int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public Color getColor() {
        switch (this) {
            case IONIAN:
                return new Color(200, 200, 50);
            case DORIAN:
                return new Color(255, 50, 50);
            case PHRYGIAN:
                return new Color(200, 50, 200);
            case LYDIAN:
                return new Color(250, 150, 30);
            case MIXOLYDIAN:
                return new Color(50, 160, 200);
            case AEOLIAN:
                return new Color(50, 70, 200);
            case LOCRIAN:
            default:
                return new Color(50, 200, 50);
        }
    }

    public int[] getTonesAndSemitones() {
        switch (this) {
            default:
            case IONIAN:
                //Starting at A: A, B, C#, D, E, F#, G#
                return new int[]{2, 2, 1, 2, 2, 2, 1};
            case DORIAN:
                //Starting at A: A, B, C, D, E, F#, G
                return new int[]{2, 1, 2, 2, 2, 1, 2};
            case PHRYGIAN:
                //Starting at A: A, A#, C, D, E, F, G
                return new int[]{1, 2, 2, 2, 1, 2, 2};
            case LYDIAN:
                //Starting at A: A, B, C#, D#, E, F#, G#
                return new int[]{2, 2, 2, 1, 2, 2, 1};
            case MIXOLYDIAN:
                //Starting at A: A, B, C#, D, E, F#, G
                return new int[]{2, 2, 1, 2, 2, 1, 2};
            case AEOLIAN:
                //Starting at A: A, B, C, D, E, F, G
                return new int[]{2, 1, 2, 2, 1, 2, 2};
            case LOCRIAN:
                //Starting at A: A, A#, C, D, D#, F, G
                return new int[]{1, 2, 2, 1, 2, 2, 2};
        }
    }

    public MusicalNote[] getNotes() {
        return getNotes(MusicalNote.A);
    }

    public MusicalNote[] getNotes(MusicalNote rootNote) {
        MusicalNote[] notes = new MusicalNote[8];
        notes[0] = rootNote;
        int[] tonesAndSemitones = this.getTonesAndSemitones();
//        Log.l("Mode: " + this);
//        Log.ln("Root note: " + rootNote);
        for (int i = 0; i < tonesAndSemitones.length; i++) {
            notes[i + 1] = MusicalNote.values()[(notes[i].getValue() + tonesAndSemitones[i]) % MusicalNote.values().length];
//            Log.l("Note " + i + ": " + notes[i]);
        }
        return notes;
    }

    public MusicalNote getRandomNote(MusicalNote rootNote) {
        MusicalNote[] notes = this.getNotes(rootNote);
        return notes[(int) (MathUtils.random(0, notes.length - 1))];
    }

    public Sprite getSprite() {
        switch (this) {
            case IONIAN:
                return SpriteManager.getInstance().IONIAN_ICON;
            case DORIAN:
                return SpriteManager.getInstance().DORIAN_ICON;
            case PHRYGIAN:
                return SpriteManager.getInstance().PHRYGIAN_ICON;
            case LYDIAN:
                return SpriteManager.getInstance().LYDIAN_ICON;
            case MIXOLYDIAN:
                return SpriteManager.getInstance().MIXOLYDIAN_ICON;
            case AEOLIAN:
                return SpriteManager.getInstance().AEOLIAN_ICON;
            case LOCRIAN:
            default:
                return SpriteManager.getInstance().LOCRIAN_ICON;
        }
    }
}
