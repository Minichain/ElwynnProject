package entities;

import audio.OpenALManager;

public enum MusicalNote {
    A(0),
    A_SHARP(1),
    B(2),
    C(3),
    C_SHARP(4),
    D(5),
    D_SHARP(6),
    E(7),
    F(8),
    F_SHARP(9),
    G(10),
    G_SHARP(11);

    private int value;

    MusicalNote(final int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public void play() {
//        Log.l("Play musical note: " + this);
        switch (this) {
            case A:
                OpenALManager.playSound(OpenALManager.SOUND_NOTE_A);
                break;
            case A_SHARP:
                OpenALManager.playSound(OpenALManager.SOUND_NOTE_A_SHARP);
                break;
            case B:
                OpenALManager.playSound(OpenALManager.SOUND_NOTE_B);
                break;
            case C:
                OpenALManager.playSound(OpenALManager.SOUND_NOTE_C);
                break;
            case C_SHARP:
                OpenALManager.playSound(OpenALManager.SOUND_NOTE_C_SHARP);
                break;
            case D:
                OpenALManager.playSound(OpenALManager.SOUND_NOTE_D);
                break;
            case D_SHARP:
                OpenALManager.playSound(OpenALManager.SOUND_NOTE_D_SHARP);
                break;
            case E:
                OpenALManager.playSound(OpenALManager.SOUND_NOTE_E);
                break;
            case F:
                OpenALManager.playSound(OpenALManager.SOUND_NOTE_F);
                break;
            case F_SHARP:
                OpenALManager.playSound(OpenALManager.SOUND_NOTE_F_SHARP);
                break;
            case G:
                OpenALManager.playSound(OpenALManager.SOUND_NOTE_G);
                break;
            case G_SHARP:
            default:
                OpenALManager.playSound(OpenALManager.SOUND_NOTE_G_SHARP);
                break;
        }
    }
}