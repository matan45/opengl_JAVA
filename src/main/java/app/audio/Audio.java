package app.audio;

import app.math.components.Camera;
import app.utilities.logger.LogInfo;
import app.utilities.resource.ResourceManager;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Path;
import java.util.*;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL11.AL_EXPONENT_DISTANCE;
import static org.lwjgl.openal.ALC.createCapabilities;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.EXTThreadLocalContext.alcSetThreadContext;
import static org.lwjgl.system.MemoryUtil.NULL;


public class Audio {

    private static List<Integer> buffers;
    private static long device;
    private static long context;
    private static Set<SoundEffect> soundEffectSet;

    private Audio() {
    }

    public static void add(SoundEffect soundEffect) {
        soundEffectSet.add(soundEffect);
    }

    public static void remove(SoundEffect soundEffect) {
        soundEffectSet.remove(soundEffect);
    }

    public static void init() {
        buffers = new ArrayList<>();
        soundEffectSet = new HashSet<>();
        device = alcOpenDevice((ByteBuffer) null);
        if (device == NULL) {
            throw new IllegalStateException("Failed to open the default device.");
        }

        ALCCapabilities deviceCaps = createCapabilities(device);

        String defaultDeviceSpecifier = Objects.requireNonNull(alcGetString(NULL, ALC_DEFAULT_DEVICE_SPECIFIER));
        LogInfo.println("Default device: " + defaultDeviceSpecifier);

        LogInfo.println("ALC device specifier: " + alcGetString(device, ALC_DEVICE_SPECIFIER));

        context = alcCreateContext(device, (IntBuffer) null);
        checkALCError(device);

        boolean useTLC = deviceCaps.ALC_EXT_thread_local_context && alcSetThreadContext(context);

        if (!useTLC && !alcMakeContextCurrent(context)) {
            throw new IllegalStateException();
        }
        checkALCError(device);

        AL.createCapabilities(deviceCaps, MemoryUtil::memCallocPointer);

        alDistanceModel(AL_EXPONENT_DISTANCE);
    }

    private static void checkALCError(long device) {
        int err = alcGetError(device);
        if (err != ALC_NO_ERROR) {
            throw new RuntimeException(alcGetString(device, err));
        }
    }

    public static int loadSource(Path path) {
        int buffer = alGenBuffers();
        buffers.add(buffer);
        try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
            ShortBuffer pcm = ResourceManager.loadAudio(path, info);

            alBufferData(buffer, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm,
                    info.sample_rate());
        }
        return buffer;

    }

    public static void billboards(Camera camera) {
        soundEffectSet.forEach(s -> s.renderBillboards(camera));
    }

    public static void cleanUp() {
        for (int buffer : buffers)
            alDeleteBuffers(buffer);
        alcMakeContextCurrent(NULL);
        alcDestroyContext(context);
        alcCloseDevice(device);
    }
}
