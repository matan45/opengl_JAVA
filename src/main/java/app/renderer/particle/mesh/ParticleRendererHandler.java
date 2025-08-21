package app.renderer.particle.mesh;

import app.math.components.Camera;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;
import app.renderer.ibl.SkyBox;
import app.renderer.lights.LightHandler;

import java.util.HashSet;
import java.util.Set;

public class ParticleRendererHandler {
    private final Camera editorCamera;
    private final Textures textures;
    private final OpenGLObjects openGLObjects;
    private final SkyBox skyBox;
    private final LightHandler lightHandler;

    private final Set<ParticleRenderer> meshRenderers;

    public ParticleRendererHandler(Camera editorCamera, Textures textures, OpenGLObjects openGLObjects, SkyBox skyBox, LightHandler lightHandler) {
        this.editorCamera = editorCamera;
        this.textures = textures;
        this.openGLObjects = openGLObjects;
        this.skyBox = skyBox;
        this.lightHandler = lightHandler;
        meshRenderers = new HashSet<>();
    }

    public ParticleRenderer createNewInstant() {
        ParticleRenderer particleRenderer = new ParticleRenderer(editorCamera, openGLObjects, textures, skyBox, lightHandler);
        meshRenderers.add(particleRenderer);
        return particleRenderer;
    }

    public void removeInstant(ParticleRenderer particleRenderer) {
        meshRenderers.remove(particleRenderer);
    }

    public void renderers() {
        meshRenderers.forEach(ParticleRenderer::renderer);
    }
}
