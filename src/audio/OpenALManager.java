package audio;

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

    public static Sound SOUND_PLAYER_HURT_01;
    public static Sound SOUND_ATTACK_01;
    public static Sound SOUND_PLAYER_DYING_01;
    public static Sound SOUND_MUSIC_O1;

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

        SOUND_ATTACK_01 = new Sound(loadSound("link_dash"), listOfSounds.size(), Sound.SoundType.EFFECT);
        listOfSounds.add(SOUND_ATTACK_01);

        SOUND_MUSIC_O1 = new Sound(loadSound("minecraft_music_01"), listOfSounds.size(), Sound.SoundType.MUSIC);
        listOfSounds.add(SOUND_MUSIC_O1);

        SOUND_PLAYER_HURT_01 = new Sound(loadSound("minecraft_player_hurt"), listOfSounds.size(), Sound.SoundType.EFFECT);
        listOfSounds.add(SOUND_PLAYER_HURT_01);

        SOUND_PLAYER_DYING_01 = new Sound(loadSound("minecraft_player_die"), listOfSounds.size(), Sound.SoundType.EFFECT);
        listOfSounds.add(SOUND_PLAYER_DYING_01);

        source = BufferUtils.createIntBuffer(listOfSounds.size());
    }

    private static int loadSound(String soundName) {
        String fileName = "res/sounds/" + soundName + ".ogg";

        //Allocate space to store return information from the function
        stackPush();
        IntBuffer channelsBuffer = stackMallocInt(1);
        stackPush();
        IntBuffer sampleRateBuffer = stackMallocInt(1);

        ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(fileName, channelsBuffer, sampleRateBuffer);

        //Retreive the extra information that was stored in the buffers by the function
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
        alSourcePlay(source.get(soundBuffer.getIndex()));
    }

    public static void onMusicLevelChange(float soundLevel) {
        for (Sound listOfSound : listOfSounds) {
            if (listOfSound.getType() == Sound.SoundType.MUSIC) {
                alSourcef(source.get(listOfSound.getIndex()), AL_GAIN, soundLevel);
            }
        }
    }

    public static void onEffectLevelChange(float soundLevel) {
        for (Sound listOfSound : listOfSounds) {
            if (listOfSound.getType() == Sound.SoundType.EFFECT) {
                alSourcef(source.get(listOfSound.getIndex()), AL_GAIN, soundLevel);
            }
        }
    }

    public static void destroy() {
        alcDestroyContext(context);
        alcCloseDevice(device);
    }
}
