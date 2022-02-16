package app.renderer.pbr;

import app.math.components.Camera;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;
import app.renderer.ibl.SkyBox;

import java.util.ArrayList;
import java.util.List;

public class MeshRendererHandler {

    private final Camera editorCamera;
    private final Textures textures;
    private final OpenGLObjects openGLObjects;
    private final SkyBox skyBox;

    private final List<MeshRenderer> meshRenderers;

    public MeshRendererHandler(Camera editorCamera, Textures textures, OpenGLObjects openGLObjects, SkyBox skyBox) {
        this.editorCamera = editorCamera;
        this.textures = textures;
        this.openGLObjects = openGLObjects;
        this.skyBox = skyBox;
        meshRenderers = new ArrayList<>();
    }

    public MeshRenderer createNewInstant() {
        return new MeshRenderer(editorCamera, openGLObjects, textures, skyBox);
    }

    public void addInstant(MeshRenderer meshRenderer) {
        meshRenderers.add(meshRenderer);
    }


    public void removeInstant(MeshRenderer meshRenderer) {
        meshRenderers.remove(meshRenderer);
    }

    public boolean isContains(MeshRenderer meshRenderer) {
        return meshRenderers.contains(meshRenderer);
    }


    public void renderers() {
        meshRenderers.forEach(MeshRenderer::renderer);
    }
}
