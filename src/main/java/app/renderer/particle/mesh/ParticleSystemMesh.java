package app.renderer.particle.mesh;

import app.math.components.Camera;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;
import app.renderer.ibl.SkyBox;
import app.renderer.lights.LightHandler;

import java.util.ArrayList;
import java.util.List;

public class ParticleSystemMesh {
    
    private static final List<ParticleEmitterMesh> emitters = new ArrayList<>();
    
    private static Camera camera;
    private static OpenGLObjects openGLObjects;
    private static Textures textures;
    private static SkyBox skyBox;
    private static LightHandler lightHandler;
    
    public static void init(Camera camera, OpenGLObjects openGLObjects, Textures textures, 
                           SkyBox skyBox, LightHandler lightHandler) {
        ParticleSystemMesh.camera = camera;
        ParticleSystemMesh.openGLObjects = openGLObjects;
        ParticleSystemMesh.textures = textures;
        ParticleSystemMesh.skyBox = skyBox;
        ParticleSystemMesh.lightHandler = lightHandler;
    }
    
    public static ParticleEmitterMesh createEmitter() {
        ParticleEmitterMesh emitter = new ParticleEmitterMesh(camera, openGLObjects, textures, skyBox, lightHandler);
        emitters.add(emitter);
        return emitter;
    }
    
    public static void update(float dt) {
        emitters.forEach(e -> e.update(dt));
    }
    
    public static void render() {
        emitters.forEach(ParticleEmitterMesh::render);
    }
    
    public static void remove(ParticleEmitterMesh emitter) {
        emitter.cleanUp();
        emitters.remove(emitter);
    }
    
    public static void cleanUp() {
        emitters.forEach(ParticleEmitterMesh::cleanUp);
        emitters.clear();
    }
    
    public static List<ParticleEmitterMesh> getEmitters() {
        return emitters;
    }
    
    public static boolean hasEmitter(ParticleEmitterMesh emitter) {
        return emitters.contains(emitter);
    }
}