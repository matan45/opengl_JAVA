package app.aduio;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alDistanceModel;
import static org.lwjgl.openal.AL11.AL_EXPONENT_DISTANCE;
import static org.lwjgl.openal.ALC.createCapabilities;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.EXTThreadLocalContext.alcSetThreadContext;
import static org.lwjgl.system.MemoryUtil.NULL;


public class Aduio {

    private static List<Integer> buffers;
    private static long device;
    private static long context;

    public static void init() {
        buffers = new ArrayList<>();
        device = alcOpenDevice((ByteBuffer) null);
        if (device == 0) {
            throw new IllegalStateException("Failed to open the default device.");
        }

        createCapabilities(device);

        context = alcCreateContext(device, (IntBuffer) null);
        alcSetThreadContext(context);

        alDistanceModel(AL_EXPONENT_DISTANCE);
    }

    public static void cleanUp() {
        for (int buffer : buffers)
            alDeleteBuffers(buffer);
        alcMakeContextCurrent(NULL);
        alcDestroyContext(context);
        alcCloseDevice(device);
    }
}
