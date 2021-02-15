package audio;

import entities.MusicalMode;
import main.Parameters;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.libc.LibCStdlib.free;

public class OpenALManager {
    private static long device;
    private static long context;

    /** SOUNDS **/
    private static ArrayList<Sound> listOfSounds;

    /** EFFECTS **/
    public static Sound SOUND_PLAYER_HURT_01;
    public static Sound SOUND_PLAYER_DYING_01;

    public static Sound SOUND_ENEMY_HURT_01;

    public static Sound SOUND_FOOTSTEP_01;
    public static Sound SOUND_FOOTSTEP_02;

    public static Sound SOUND_ROLLING_01;

    public static Sound SOUND_GOLD_COIN_PICKED_UP_01;
    public static Sound SOUND_CASH_01;
    public static Sound SOUND_DRINK_01;

    public static Sound SOUND_NOTE_A;
    public static Sound SOUND_NOTE_A_SHARP;
    public static Sound SOUND_NOTE_B;
    public static Sound SOUND_NOTE_C;
    public static Sound SOUND_NOTE_C_SHARP;
    public static Sound SOUND_NOTE_D;
    public static Sound SOUND_NOTE_D_SHARP;
    public static Sound SOUND_NOTE_E;
    public static Sound SOUND_NOTE_F;
    public static Sound SOUND_NOTE_F_SHARP;
    public static Sound SOUND_NOTE_G;
    public static Sound SOUND_NOTE_G_SHARP;

    public static Sound SOUND_CHANGE_MUSICAL_MODE_IONIAN;
    public static Sound SOUND_CHANGE_MUSICAL_MODE_DORIAN;
    public static Sound SOUND_CHANGE_MUSICAL_MODE_PHRYGIAN;
    public static Sound SOUND_CHANGE_MUSICAL_MODE_LYDIAN;
    public static Sound SOUND_CHANGE_MUSICAL_MODE_MIXOLYDIAN;
    public static Sound SOUND_CHANGE_MUSICAL_MODE_AEOLIAN;
    public static Sound SOUND_CHANGE_MUSICAL_MODE_LOCRIAN;

    /** AMBIENCE **/
    public static Sound SOUND_RAIN_01;

    /** MUSIC **/
    public static Sound SOUND_MUSIC_A_IONIAN_O1;
    public static Sound SOUND_MUSIC_A_IONIAN_O2;
    public static Sound SOUND_MUSIC_A_DORIAN_O1;
    public static Sound SOUND_MUSIC_A_PHRYGIAN_O1;

    /** Sources are points emitting sound. */
    private static IntBuffer source;

    /** Position of the listener. */
    private static FloatBuffer listenerPos = BufferUtils.createFloatBuffer(3).put(new float[] {0.0f, 0.0f});

    public static void prepareOpenAL() {
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        device = alcOpenDevice(defaultDeviceName);

        int[] attributes = {0};
        context = alcCreateContext(device, attributes);
        alcMakeContextCurrent(context);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(device);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        if (alCapabilities.OpenAL10) {
            //OpenAL 1.0 is supported
        }

        loadSounds();

        setupSources();
        setupListener();
    }

    private static void loadSounds() {
        listOfSounds = new ArrayList<>();

        /** EFFECTS **/

        String instrumentSoundFolder = "instruments/guitar01/";
        SOUND_NOTE_A = loadSound(instrumentSoundFolder + "note_a", Sound.SoundType.EFFECT);
        SOUND_NOTE_A_SHARP = loadSound(instrumentSoundFolder + "note_a_sharp", Sound.SoundType.EFFECT);
        SOUND_NOTE_B = loadSound(instrumentSoundFolder + "note_b", Sound.SoundType.EFFECT);
        SOUND_NOTE_C = loadSound(instrumentSoundFolder + "note_c", Sound.SoundType.EFFECT);
        SOUND_NOTE_C_SHARP = loadSound(instrumentSoundFolder + "note_c_sharp", Sound.SoundType.EFFECT);
        SOUND_NOTE_D = loadSound(instrumentSoundFolder + "note_d", Sound.SoundType.EFFECT);
        SOUND_NOTE_D_SHARP = loadSound(instrumentSoundFolder + "note_d_sharp", Sound.SoundType.EFFECT);
        SOUND_NOTE_E = loadSound(instrumentSoundFolder + "note_e", Sound.SoundType.EFFECT);
        SOUND_NOTE_F = loadSound(instrumentSoundFolder + "note_f", Sound.SoundType.EFFECT);
        SOUND_NOTE_F_SHARP = loadSound(instrumentSoundFolder + "note_f_sharp", Sound.SoundType.EFFECT);
        SOUND_NOTE_G = loadSound(instrumentSoundFolder + "note_g", Sound.SoundType.EFFECT);
        SOUND_NOTE_G_SHARP = loadSound(instrumentSoundFolder + "note_g_sharp", Sound.SoundType.EFFECT);

        SOUND_CHANGE_MUSICAL_MODE_IONIAN = loadSound("change_musical_mode_ionian", Sound.SoundType.EFFECT);
        SOUND_CHANGE_MUSICAL_MODE_DORIAN = loadSound("change_musical_mode_dorian", Sound.SoundType.EFFECT);
        SOUND_CHANGE_MUSICAL_MODE_PHRYGIAN = loadSound("change_musical_mode_phrygian", Sound.SoundType.EFFECT);
        SOUND_CHANGE_MUSICAL_MODE_LYDIAN = loadSound("change_musical_mode_lydian", Sound.SoundType.EFFECT);
        SOUND_CHANGE_MUSICAL_MODE_MIXOLYDIAN = loadSound("change_musical_mode_mixolydian", Sound.SoundType.EFFECT);
        SOUND_CHANGE_MUSICAL_MODE_AEOLIAN = loadSound("change_musical_mode_aeolian", Sound.SoundType.EFFECT);
        SOUND_CHANGE_MUSICAL_MODE_LOCRIAN = loadSound("change_musical_mode_locrian", Sound.SoundType.EFFECT);

        SOUND_PLAYER_HURT_01 = loadSound("player_hurt_01", Sound.SoundType.EFFECT);
        SOUND_PLAYER_DYING_01 = loadSound("player_die_01", Sound.SoundType.EFFECT);

        SOUND_ENEMY_HURT_01 = loadSound("enemy_hurt_01", Sound.SoundType.EFFECT);

        SOUND_FOOTSTEP_01 = loadSound("footstep_01", Sound.SoundType.EFFECT);
        SOUND_FOOTSTEP_02 = loadSound("footstep_02", Sound.SoundType.EFFECT);

        SOUND_ROLLING_01 = loadSound("rolling_01", Sound.SoundType.EFFECT);

        SOUND_GOLD_COIN_PICKED_UP_01 = loadSound("gold_coin_picked_up_01", Sound.SoundType.EFFECT);

        SOUND_CASH_01 = loadSound("cash_01", Sound.SoundType.EFFECT);

        SOUND_DRINK_01 = loadSound("drink_01", Sound.SoundType.EFFECT);

        /** AMBIENCE **/
        SOUND_RAIN_01 = loadSound("rain_01", Sound.SoundType.AMBIENCE);

        /** MUSIC **/
        SOUND_MUSIC_A_IONIAN_O1 = loadSound("music_a_ionian_01", Sound.SoundType.MUSIC);
        SOUND_MUSIC_A_IONIAN_O2 = loadSound("music_a_ionian_02", Sound.SoundType.MUSIC);
        SOUND_MUSIC_A_DORIAN_O1 = loadSound("music_a_dorian_01", Sound.SoundType.MUSIC);
        SOUND_MUSIC_A_PHRYGIAN_O1 = loadSound("music_a_phrygian_01", Sound.SoundType.MUSIC);

        source = BufferUtils.createIntBuffer(listOfSounds.size());
    }

    private static Sound loadSound(String soundName, Sound.SoundType soundType) {
        Sound sound = null;
        try {
            switch (soundType) {
                case EFFECT:
                    soundName = "effects/" + soundName;
                    break;
                case MUSIC:
                    soundName = "music/" + soundName;
                    break;
                case AMBIENCE:
                    soundName = "ambience/" + soundName;
                    break;
            }
            sound = new Sound(loadSound(soundName), listOfSounds.size(), soundType);
            listOfSounds.add(sound);
        } catch (Exception e) {
            System.err.println("Error loading " + soundName);
        }
        return sound;
    }

    private static int loadSound(String soundName) {
        String fileName = "res/sounds/" + soundName + ".ogg";

        //Allocate space to store return information from the function
        stackPush();
        IntBuffer channelsBuffer = stackMallocInt(1);
        stackPush();
        IntBuffer sampleRateBuffer = stackMallocInt(1);

        ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(fileName, channelsBuffer, sampleRateBuffer);

        //Retrieve the extra information that was stored in the buffers by the function
        int channels = channelsBuffer.get();
        int sampleRate = sampleRateBuffer.get();
        //Free the space we allocated earlier
        stackPop();
        stackPop();

        //Find the correct OpenAL format
        int format = -1;
        if(channels == 1) {
            format = AL_FORMAT_MONO16;
        } else if(channels == 2) {
            format = AL_FORMAT_STEREO16;
        }

        //Request space for the buffer
        int bufferPointer = alGenBuffers();

        //Send the data to OpenAL
        alBufferData(bufferPointer, format, rawAudioBuffer, sampleRate);

        //Free the memory allocated by STB
        free(rawAudioBuffer);

        return bufferPointer;
    }

    private static void setupSources() {
        alGenSources(source);
        for (int i = 0; i < listOfSounds.size(); i++) {
            float gain;
            switch (listOfSounds.get(i).getType()) {
                case EFFECT:
                    gain = Parameters.getEffectSoundLevel();
                    break;
                case MUSIC:
                    gain = Parameters.getMusicSoundLevel();
                    break;
                case AMBIENCE:
                default:
                    gain = Parameters.getAmbienceSoundLevel();
                    break;
            }
            setupSource(listOfSounds.get(i).getBuffer(), listOfSounds.get(i).getIndex(), gain);
        }
    }

    private static void setupSource(int soundBuffer, int index, float gain) {
        alSourcei(source.get(index), AL_BUFFER, soundBuffer);
        alSourcef(source.get(index), AL_GAIN, gain);
    }

    private static void setupListener() {
        alListenerfv(AL_POSITION, listenerPos);
    }

    public static void playSound(Sound soundBuffer) {
        if (soundBuffer == null || (isPlaying(soundBuffer) && soundBuffer.getType() != Sound.SoundType.EFFECT)) {
            return;
        }
        alSourcePlay(source.get(soundBuffer.getIndex()));
    }

    public static boolean isPlaying(Sound soundBuffer) {
        return AL10.alGetSourcei(source.get(soundBuffer.getIndex()), AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
    }

    public static void stopPlayingSound(Sound soundBuffer) {
        if (soundBuffer != null) {
            alSourceStop(source.get(soundBuffer.getIndex()));
        }
    }

    public static void setSoundGain(Sound soundBuffer, float gain) {
        if (soundBuffer != null) {
            alSourcef(source.get(soundBuffer.getIndex()), AL_GAIN, gain);
        }
    }

    public static void onMusicLevelChange(float soundLevel) {
        if (listOfSounds != null) {
            for (Sound sound : listOfSounds) {
                if (sound.getType() == Sound.SoundType.MUSIC) {
                    alSourcef(source.get(sound.getIndex()), AL_GAIN, soundLevel);
                }
            }
        }
    }

    public static void onEffectLevelChange(float soundLevel) {
        if (listOfSounds != null) {
            for (Sound sound : listOfSounds) {
                if (sound.getType() == Sound.SoundType.EFFECT) {
                    alSourcef(source.get(sound.getIndex()), AL_GAIN, soundLevel);
                }
            }
        }
    }

    public static void onAmbienceLevelChange(float soundLevel) {
        if (listOfSounds != null) {
            for (Sound sound : listOfSounds) {
                if (sound.getType() == Sound.SoundType.AMBIENCE) {
                    alSourcef(source.get(sound.getIndex()), AL_GAIN, soundLevel);
                }
            }
        }
    }

    public static void destroy() {
        alcDestroyContext(context);
        alcCloseDevice(device);
    }

    public static void playMusicDependingOnMusicalMode(MusicalMode musicalMode) {
        switch (musicalMode) {
            case IONIAN:
            default:
                OpenALManager.playSound(OpenALManager.SOUND_MUSIC_A_IONIAN_O2);
                break;
//            case DORIAN:
//                OpenALManager.playSound(OpenALManager.SOUND_MUSIC_A_DORIAN_O1);
//                break;
//            case PHRYGIAN:
//            default:
//                OpenALManager.playSound(OpenALManager.SOUND_MUSIC_A_PHRYGIAN_O1);
//                break;
        }
    }

    public static void stopMusicDependingOnMusicalMode(MusicalMode musicalMode) {
        switch (musicalMode) {
            case IONIAN:
            default:
                OpenALManager.stopPlayingSound(OpenALManager.SOUND_MUSIC_A_IONIAN_O2);
                break;
//            case DORIAN:
//                OpenALManager.stopPlayingSound(OpenALManager.SOUND_MUSIC_A_DORIAN_O1);
//                break;
//            case PHRYGIAN:
//            default:
//                OpenALManager.stopPlayingSound(OpenALManager.SOUND_MUSIC_A_PHRYGIAN_O1);
//                break;
        }
    }
}
