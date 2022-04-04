package app.audio;

import app.math.OLVector3f;
import app.math.components.Camera;
import app.renderer.debug.billboards.Billboards;

import java.nio.file.Path;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL11.AL_SAMPLE_OFFSET;
import static org.lwjgl.openal.AL11.AL_SEC_OFFSET;

public class SoundEffect {
    private final int sourceID;
    private final Billboards billboards;
    private OLVector3f position;

    public SoundEffect(Billboards billboards) {
        sourceID = alGenSources();
        // the distance from the source will be 1
        alSourcef(sourceID, AL_ROLLOFF_FACTOR, 0);
        // the speed the sound decrees
        alSourcef(sourceID, AL_REFERENCE_DISTANCE, 0);
        // the max distance for the sound to stop
        alSourcef(sourceID, AL_MAX_DISTANCE, 0);

        this.billboards = billboards;
        Audio.add(this);
    }

    public void loadSound(Path path) {
        int buffer = Audio.loadSource(path);
        alSourcei(sourceID, AL_BUFFER, buffer);
    }

    public void play() {
        alSourcePlay(sourceID);
    }

    public void delete() {
        stop();
        alDeleteSources(sourceID);
        Audio.remove(this);
    }

    public void setVelocity(OLVector3f velocity) {
        alSource3f(sourceID, AL_VELOCITY, velocity.x, velocity.y, velocity.z);
    }

    public int getFrame() {
        return (int) alGetSourcef(sourceID, AL_SAMPLE_OFFSET) / 100000;
    }

    public int getTotalFrame() {
        return (int) alGetSourcef(sourceID, AL_SEC_OFFSET);
    }

    public void pause() {
        alSourcePause(sourceID);
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

    public float getVolume() {
        return alGetSourcef(sourceID, AL_GAIN);
    }

    public void setPosition(OLVector3f position) {
        this.position = position;
        alSource3f(sourceID, AL_POSITION, position.x, position.y, position.z);
    }

    public void renderBillboards(Camera camera) {
        billboards.render(camera, position);
    }

}
