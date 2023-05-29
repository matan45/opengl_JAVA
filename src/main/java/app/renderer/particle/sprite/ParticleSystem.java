package app.renderer.particle.sprite;

import app.renderer.OpenGLObjects;

import java.util.ArrayList;
import java.util.List;

public class ParticleSystem {

    private static final List<ParticleEmitter> emitters = new ArrayList<>();

    private static OpenGLObjects openGLObjects;

    public static void init(OpenGLObjects openGLObjects) {
        ParticleSystem.openGLObjects = openGLObjects;
    }

    public static ParticleEmitter createEmitter() {
        ParticleEmitter particleEmitter = new ParticleEmitter(openGLObjects);
        emitters.add(particleEmitter);
        return particleEmitter;
    }

    public static void update(float dt) {
        emitters.forEach(e -> e.update(dt));
    }

    public static void add(ParticleEmitter particleEmitter) {
        emitters.add(particleEmitter);
    }

    public static void remove(ParticleEmitter particleEmitter) {
        emitters.remove(particleEmitter);
    }


    public static void render() {
        emitters.forEach(ParticleEmitter::render);
    }

    public static void cleanUp() {
        emitters.forEach(ParticleEmitter::cleanUp);
        emitters.clear();
    }
}
