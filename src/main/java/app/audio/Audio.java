package app.audio;

import app.utilities.resource.ResourceManager;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC.createCapabilities;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.ALC11.ALC_ALL_DEVICES_SPECIFIER;
import static org.lwjgl.openal.ALUtil.getStringList;
import static org.lwjgl.openal.EXTThreadLocalContext.alcSetThreadContext;
import static org.lwjgl.system.MemoryUtil.NULL;


public class Audio {

    private static List<Integer> buffers;
    private static long device;
    private static long context;

    private Audio() {
    }

    public static void init() {
        buffers = new ArrayList<>();
        device = alcOpenDevice((ByteBuffer) null);
        if (device == NULL) {
            throw new IllegalStateException("Failed to open the default device.");
        }

        ALCCapabilities deviceCaps = createCapabilities(device);

        List<String> devices = getStringList(NULL, ALC_ALL_DEVICES_SPECIFIER);

        for (int i = 0; i < Objects.requireNonNull(devices).size(); i++) {
            System.out.println(i + ": " + devices.get(i));
        }

        String defaultDeviceSpecifier = Objects.requireNonNull(alcGetString(NULL, ALC_DEFAULT_DEVICE_SPECIFIER));
        System.out.println("Default device: " + defaultDeviceSpecifier);

        System.out.println("ALC device specifier: " + alcGetString(device, ALC_DEVICE_SPECIFIER));

        context = alcCreateContext(device, (IntBuffer) null);
        checkALCError(device);

        boolean useTLC = deviceCaps.ALC_EXT_thread_local_context && alcSetThreadContext(context);
        if (!useTLC) {
            if (!alcMakeContextCurrent(context)) {
                throw new IllegalStateException();
            }
        }
        checkALCError(device);

        AL.createCapabilities(deviceCaps, MemoryUtil::memCallocPointer);

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

    public static void cleanUp() {
        for (int buffer : buffers)
            alDeleteBuffers(buffer);
        alcMakeContextCurrent(NULL);
        alcDestroyContext(context);
        alcCloseDevice(device);
    }
}
