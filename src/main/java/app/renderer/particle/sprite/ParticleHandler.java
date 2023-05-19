package app.renderer.particle.sprite;

import app.renderer.OpenGLObjects;

import java.util.ArrayList;
import java.util.List;

public class ParticleHandler {
    private static ParticleRendererSprite particleRendererSprite;
    private static final List<Particle> particles = new ArrayList<>();

    public static void init(OpenGLObjects openGLObjects) {
        particleRendererSprite = new ParticleRendererSprite(openGLObjects);
    }

    public static void update(float dt) {
        particles.forEach(p -> p.update(dt));
    }

    public static void render() {
        particleRendererSprite.render(particles);
    }

    public static void add(Particle particle) {
        particles.add(particle);
    }
}
