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
        try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(path.toAbsolutePath().toString()))) {
            close();
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }


    public void play() {
        if (clip != null) {
            clip.setFramePosition(lastFrame);
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
        if (clip != null) {
            stop();
            clip.close();
        }
    }

    public void setVolume(float volume) {
        if (control != null)
            control.setValue(volume);
    }

    public float getMaxVolume() {
        if (control != null)
            return control.getMaximum();
        return 0;
    }

    public float getMinVolume() {
        if (control != null)
            return control.getMinimum();
        return 0;
    }

    public float getVolume() {
        if (control != null)
            return control.getValue();
        return 0;
    }

    public void setLooping(boolean loop) {
        if (clip != null) {
            if (loop)
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            else
                clip.loop(1);
        }
    }


    public int getFrame() {
        if (clip != null)
            return (int) (clip.getMicrosecondPosition() / 1000000);
        return 0;
    }

    public int getTotalFrame() {
        if (clip != null)
            return clip.getFrameLength() / 10000;
        return 0;
    }

    public void setFramePosition(int frame) {
        if (clip != null) {
            clip.setFramePosition(frame);
            lastFrame = frame;
        }

    }


    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.setFramePosition(0);
            lastFrame = 0;
        }
    }

}
