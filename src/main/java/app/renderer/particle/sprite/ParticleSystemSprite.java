package app.renderer.particle.sprite;

import app.renderer.OpenGLObjects;
import app.renderer.Textures;

import java.util.ArrayList;
import java.util.List;

public class ParticleSystemSprite {

    private static final List<ParticleEmitterSprite> emitters = new ArrayList<>();

    private static OpenGLObjects openGLObjects;
    private static Textures textures;

    public static void init(OpenGLObjects openGLObjects, Textures textures) {
        ParticleSystemSprite.openGLObjects = openGLObjects;
        ParticleSystemSprite.textures = textures;
    }

    public static ParticleEmitterSprite createEmitter() {
        ParticleEmitterSprite particleEmitter = new ParticleEmitterSprite(openGLObjects, textures);
        emitters.add(particleEmitter);
        return particleEmitter;
    }

    public static void update(float dt) {
        emitters.forEach(e -> e.update(dt));
    }


    public static void remove(ParticleEmitterSprite particleEmitter) {
        particleEmitter.cleanUp();
        emitters.remove(particleEmitter);
    }


    public static void render() {
        emitters.forEach(ParticleEmitterSprite::render);
    }

    public static void cleanUp() {
        emitters.forEach(ParticleEmitterSprite::cleanUp);
        emitters.clear();
    }
}
