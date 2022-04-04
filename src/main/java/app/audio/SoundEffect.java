package app.audio;

import app.math.OLVector3f;

import java.nio.file.Path;

import static org.lwjgl.openal.AL10.*;

public class SoundEffect {
    private final int sourceID;
    private int buffer;
    private String path;

    public SoundEffect() {
        sourceID = alGenSources();
        // the distance from the source will be 1
        alSourcef(sourceID, AL_ROLLOFF_FACTOR, 0);
        // the speed the sound decrees
        alSourcef(sourceID, AL_REFERENCE_DISTANCE, 0);
        // the max distance for the sound to stop
        alSourcef(sourceID, AL_MAX_DISTANCE, 0);
    }

    public void loadSound(Path path) {

        buffer = Audio.loadSource(path);
        this.path = path.toAbsolutePath().toString();
    }

    public void play() {
        stop();
        alSourcei(sourceID, AL_BUFFER, buffer);
        alSourcePlay(sourceID);
    }

    public void delete() {
        stop();
        alDeleteSources(sourceID);
    }

    public void setVelocity(OLVector3f velocity) {
        alSource3f(sourceID, AL_VELOCITY, velocity.x, velocity.y, velocity.z);
    }

    public void pause() {
        alSourcePause(sourceID);
    }

    public void continuePlaying() {
        alSourcePlay(sourceID);
    }

    public void stop() {
        alSourceStop(sourceID);
    }

    public void setLooping(boolean loop) {
        alSourcei(sourceID, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
    }

    public boolean isPlaying() {
        return alGetSourcei(sourceID, AL_SOURCE_STATE) == AL_PLAYING;
    }

    public void setVolume(float volume) {
        alSourcef(sourceID, AL_GAIN, volume);
    }

    public void setPosition(OLVector3f position) {
        alSource3f(sourceID, AL_POSITION, position.x, position.y, position.z);
    }

    public String getPath() {
        return path;
    }
}
