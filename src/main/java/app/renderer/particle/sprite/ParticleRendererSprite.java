package app.renderer.particle.sprite;

import app.math.components.Camera;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;
import app.renderer.VaoModel;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.List;

public class ParticleRendererSprite {
    private static final float[] VERTICES = {-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f};
    private static int MAX_INSTANCES = 10000;
    private static int INSTANCE_DATA_LENGTH = 21;
    private static final FloatBuffer buffer = BufferUtils.createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH);
    private List<Particle> particles;
    private int vbo;
    private Camera camera;
    private OpenGLObjects openGLObjects;
    private Textures textures;
    private VaoModel vaoModel;

    public ParticleRendererSprite(Camera camera, OpenGLObjects openGLObjects, Textures textures) {
        this.camera = camera;
        this.openGLObjects = openGLObjects;
        vbo = openGLObjects.createEmptyVbo(INSTANCE_DATA_LENGTH * MAX_INSTANCES);
        vaoModel = openGLObjects.loadToVAO(VERTICES, 2);
    }
}
