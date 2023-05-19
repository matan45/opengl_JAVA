package app.renderer.particle.sprite;

import java.util.List;

public class ParticleHandler {
    private static ParticleRendererSprite particleRendererSprite;
    private static List<Particle> particles;

    public static void update(float dt) {

    }

    public static void add(Particle particle) {
        particles.add(particle);
    }
}
