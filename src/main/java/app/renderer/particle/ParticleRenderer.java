package app.renderer.particle;

import app.math.components.Camera;
import app.math.components.OLTransform;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;
import app.renderer.VaoModel;
import app.renderer.ibl.SkyBox;
import app.renderer.lights.LightHandler;
import app.renderer.pbr.Mesh;
import app.renderer.shaders.UniformsNames;

import java.nio.file.Paths;
import java.util.Objects;

public class ParticleRenderer {
    private final ParticleMaterial particleMaterial;
    private final ParticleShader particleShader;
    private final Camera camera;
    private final OpenGLObjects openGLObjects;
    private final SkyBox skyBox;
    private int select;
    private VaoModel vaoModel;
    private OLTransform olTransform;
    private final LightHandler lightHandler;

    public ParticleRenderer(Camera camera, OpenGLObjects openGLObjects, Textures textures, SkyBox skyBox, LightHandler lightHandler) {
        particleMaterial = new ParticleMaterial(textures);
        this.camera = camera;
        this.openGLObjects = openGLObjects;
        particleShader = new ParticleShader(Paths.get("src\\main\\resources\\shaders\\particle\\particle.glsl"));
        particleShader.bindBlockBuffer(UniformsNames.MATRICES.getUniformsName(), 0);

        this.skyBox = skyBox;
        this.lightHandler = lightHandler;
    }

    public void init(Mesh mesh, OLTransform olTransform) {

        vaoModel = openGLObjects.loadToVAO(mesh.vertices(), mesh.textures(), mesh.normals(), mesh.indices());
        this.olTransform = olTransform;
    }

    public void renderer() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticleRenderer that = (ParticleRenderer) o;
        return Objects.equals(this, that);
    }

    public ParticleMaterial getMaterial() {
        return particleMaterial;
    }
}
