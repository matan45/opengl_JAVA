package app.renderer.particle.sprite;

import app.renderer.OpenGLObjects;

import java.util.ArrayList;
import java.util.List;

public class ParticleSystemSprite {

    private static final List<ParticleEmitter> emitters = new ArrayList<>();

    private static OpenGLObjects openGLObjects;

    public static void init(OpenGLObjects openGLObjects) {
        ParticleSystemSprite.openGLObjects = openGLObjects;
    }

    public static ParticleEmitter createEmitter() {
        ParticleEmitter particleEmitter = new ParticleEmitter(openGLObjects);
        emitters.add(particleEmitter);
        return particleEmitter;
    }

    public static void update(float dt) {
        emitters.forEach(e -> e.update(dt));
    }


    public static void remove(ParticleEmitter particleEmitter) {
        particleEmitter.cleanUp();
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
