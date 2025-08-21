package app.renderer.particle.sprite;

import app.renderer.OpenGLObjects;
import app.renderer.Textures;
import app.renderer.particle.sprite.data.ParticleMaterialSprite;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ParticleEmitterSprite {
    private final ParticleRendererSprite particleRendererSprite;
    private final Textures textures;
    private final List<ParticleSprite> particles = new ArrayList<>();
    private int image = 0;
    private boolean pause = false;
    private boolean play = false;

    public ParticleEmitterSprite(OpenGLObjects openGLObjects, Textures textures) {
        particleRendererSprite = new ParticleRendererSprite(openGLObjects);
        this.textures = textures;
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

    public void createParticle(ParticleMaterialSprite particleMaterial) {
        image = textures.loadTexture(Path.of(particleMaterial.getTexturePath()));
        for (int i = 0; i < particleMaterial.getParticleAmount(); i++)
            particles.add(new ParticleSprite(particleMaterial));
    }

    public void setInfinity(boolean infinity) {
        particles.forEach(p -> p.setInfinity(infinity));
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
        ParticleEmitterSprite that = (ParticleEmitterSprite) o;
        return Objects.equals(this, that);
    }
}
