package app.audio;

import java.nio.file.Path;

import static org.lwjgl.openal.AL10.*;

public class SoundEffect {
    private int sourceID;
    private int buffer;

    public SoundEffect() {
        sourceID = alGenSources();
        // the distance from the source will be 1
        alSourcef(sourceID, AL_ROLLOFF_FACTOR, 0);
        // the speed the sound decrees
        alSourcef(sourceID, AL_REFERENCE_DISTANCE, 0);
        // the max distance for the sound to stop
        alSourcef(sourceID, AL_MAX_DISTANCE, 0);
    }

    public void loadSound(Path path){

    }
}
