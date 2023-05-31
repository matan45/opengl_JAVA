package app.renderer.particle.sprite;

import app.renderer.OpenGLObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ParticleEmitter {
    private final ParticleRendererSprite particleRendererSprite;
    private final List<Particle> particles = new ArrayList<>();
    private int image = 0;
    private boolean pause = false;
    private boolean play = false;

    public ParticleEmitter(OpenGLObjects openGLObjects) {
        particleRendererSprite = new ParticleRendererSprite(openGLObjects);
    }

    public void update(float dt) {
        if (!pause) {
            int numParticles = particles.size();
            // Define the batch size
            int batchSize = 64;
            for (int i = 0; i < numParticles; i += batchSize) {
                int endIndex = Math.min(i + batchSize, numParticles);
                for (int j = i; j < endIndex; j++) {
                    particles.get(j).update(dt);
                }
            }
        }
    }

    public void render() {
        if (play)
            particleRendererSprite.render(particles, image);
    }

    public void reset() {
        play = false;
        pause = true;
        cleanUp();
    }


    public void setImage(int image) {
        this.image = image;
    }

    public void createParticle(Particle particle, int amount) {
        for (int i = 0; i < amount; i++)
            particles.add(new Particle(particle));
    }

    public void setIsInfinity(boolean isInfinity) {
        particles.forEach(p -> p.setInfinity(isInfinity));
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public void setPlay(boolean play) {
        this.play = play;
    }

    public void cleanUp() {
        particles.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticleEmitter that = (ParticleEmitter) o;
        return Objects.equals(this, that);
    }
}
