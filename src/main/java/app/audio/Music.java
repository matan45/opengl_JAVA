package app.audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Music {

    private Clip clip;
    private int lastFrame;
    private FloatControl control;

    public void loadMusic(Path path) {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(path.toAbsolutePath().toString()));
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }


    public void play() {
        if (clip != null) {
            stop();
            clip.start();
        }
    }

    public boolean isDone() {
        return (clip.getFrameLength() == clip.getFramePosition());
    }

    public void pause() {
        lastFrame = clip.getFramePosition();
        clip.stop();
    }

    public void continuePlay() {
        clip.setFramePosition(lastFrame);
        clip.start();
    }

    public void close() {
        stop();
        clip.close();
    }

    public void setVolume(float volume) {
        control.setValue(volume);
    }

    public void setLooping(boolean loop) {
        if (loop)
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        else
            clip.loop(0);
    }

    public void stop() {
        clip.stop();
    }
}
