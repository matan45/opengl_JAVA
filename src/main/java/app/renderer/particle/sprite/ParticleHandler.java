package app.renderer.particle.sprite;

import app.renderer.OpenGLObjects;

import java.util.ArrayList;
import java.util.List;

public class ParticleHandler {
    private static ParticleRendererSprite particleRendererSprite;
    private static final List<Particle> particles = new ArrayList<>();
    private static final int batchSize = 64; // Define the batch size
    private static int image = 0;
    private static boolean pause = false;

    public static void init(OpenGLObjects openGLObjects) {
        particleRendererSprite = new ParticleRendererSprite(openGLObjects);
    }

    public static void update(float dt) {
        if (!pause) {
            int numParticles = particles.size();
            for (int i = 0; i < numParticles; i += batchSize) {
                int endIndex = Math.min(i + batchSize, numParticles);
                for (int j = i; j < endIndex; j++) {
                    particles.get(j).update(dt);
                }
            }
        }
    }

    public static void render() {
        particleRendererSprite.render(particles, image);
    }


    public static void setImage(int image) {
        ParticleHandler.image = image;
    }

    public static void create(Particle particle, int amount) {
        for (int i = 0; i < amount; i++)
            particles.add(new Particle(particle));
    }

    public static void setIsInfinity(boolean isInfinity) {
        particles.forEach(p -> p.setInfinity(isInfinity));
    }

    public static void setPause(boolean pause) {
        ParticleHandler.pause = pause;
    }

    public static void cleanUp() {
        particles.clear();
    }
}
