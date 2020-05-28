package entities;

import java.awt.*;

public enum MusicalMode {
    IONIAN,     //MAJOR SCALE
    DORIAN,
    PHRYGIAN,
    LYDIAN,
    MIXOLYDIAN,
    AEOLIAN,    //NATURAL MINOR SCALE
    LOCRIAN;

    public final static int numOfMusicalModes = 3;

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
            case IONIAN:
                return new int[]{2, 2, 1, 2, 2, 2, 1};
            case DORIAN:
                return new int[]{2, 1, 2, 2, 2, 1, 2};
            case PHRYGIAN:
                return new int[]{1, 2, 2, 2, 1, 2, 2};
            case LYDIAN:
                return new int[]{2, 2, 2, 1, 2, 2, 1};
            case MIXOLYDIAN:
                return new int[]{2, 2, 1, 2, 2, 1, 2};
            case AEOLIAN:
                return new int[]{2, 1, 2, 2, 1, 2, 2};
            case LOCRIAN:
            default:
                return new int[]{1, 2, 2, 1, 2, 2, 2};
        }
    }

    public MusicalNote[] getNotes(MusicalNote rootNote) {
        MusicalNote[] notes = new MusicalNote[8];
        notes[0] = rootNote;
        int[] tonesAndSemitones = this.getTonesAndSemitones();
//        System.out.println("Mode: " + this);
//        System.out.println("Root note: " + rootNote);
        for (int i = 0; i < tonesAndSemitones.length; i++) {
            notes[i + 1] = MusicalNote.values()[(notes[i].getValue() + tonesAndSemitones[i]) % MusicalNote.values().length];
//            System.out.println("Note " + i + ": " + notes[i]);
        }
        return notes;
    }

    public MusicalNote getRandomNote(MusicalNote rootNote) {
        MusicalNote[] notes = this.getNotes(rootNote);
        return notes[(int) (Math.random() * notes.length)];
    }
}
