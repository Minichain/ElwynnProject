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
}
