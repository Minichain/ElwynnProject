package main;

import org.lwjgl.openal.*;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.libc.LibCStdlib.free;

public class MyOpenAL {
    private static long device;
    private static long context;

    /** SOUNDS **/
    public static int SOUND_SECRET;
    public static int SOUND_LINK_HURT;
    public static int SOUND_LINK_DASH;
    public static int SOUND_LINK_DYING;
    public static int SOUND_OVERWORLD;

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
    }

    private static void loadSounds() {
        SOUND_SECRET = loadSound("secret");
        SOUND_LINK_HURT = loadSound("link_hurt");
        SOUND_LINK_DASH = loadSound("link_dash");
        SOUND_LINK_DYING = loadSound("link_dying");
        SOUND_OVERWORLD = loadSound("overworld");
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

    public static void playSound(int soundBuffer) {
        int sourcePointer = alGenSources();

        //Assign our buffer to the source
        alSourcei(sourcePointer, AL_BUFFER, soundBuffer);
        alSourcePlay(sourcePointer);
    }

    public static void destroy() {
        alcDestroyContext(context);
        alcCloseDevice(device);
    }
}
