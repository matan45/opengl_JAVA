package app.renderer.pbr;

import app.math.components.Camera;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;
import app.renderer.fog.Fog;
import app.renderer.ibl.SkyBox;
import app.renderer.lights.LightHandler;

import java.util.ArrayList;
import java.util.List;

public class MeshRendererHandler {

    private final Camera editorCamera;
    private final Textures textures;
    private final OpenGLObjects openGLObjects;
    private final SkyBox skyBox;
    private final LightHandler lightHandler;

    private final List<MeshRenderer> meshRenderers;

    public MeshRendererHandler(Camera editorCamera, Textures textures, OpenGLObjects openGLObjects, SkyBox skyBox, LightHandler lightHandler) {
        this.editorCamera = editorCamera;
        this.textures = textures;
        this.openGLObjects = openGLObjects;
        this.skyBox = skyBox;
        this.lightHandler = lightHandler;
        meshRenderers = new ArrayList<>();
    }

    public MeshRenderer createNewInstant() {
        MeshRenderer meshRenderer = new MeshRenderer(editorCamera, openGLObjects, textures, skyBox, lightHandler);
        meshRenderers.add(meshRenderer);
        return meshRenderer;
    }

    public void removeInstant(MeshRenderer meshRenderer) {
        meshRenderers.remove(meshRenderer);
    }

    public void setFog(Fog fog) {
        meshRenderers.forEach(m -> m.setFog(fog));
    }

    public void renderers() {
        meshRenderers.forEach(MeshRenderer::renderer);
    }

}
