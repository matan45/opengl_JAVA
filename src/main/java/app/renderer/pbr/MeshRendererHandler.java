package app.renderer.pbr;

import app.math.components.Camera;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;

import java.util.ArrayList;
import java.util.List;

public class MeshRendererHandler {

    private final Camera editorCamera;
    private final Textures textures;
    private final OpenGLObjects openGLObjects;

    private final List<MeshRenderer> meshRenderers;

    public MeshRendererHandler(Camera editorCamera, Textures textures, OpenGLObjects openGLObjects) {
        this.editorCamera = editorCamera;
        this.textures = textures;
        this.openGLObjects = openGLObjects;
        meshRenderers = new ArrayList<>();
    }

    public MeshRenderer createNewInstant() {
        MeshRenderer meshRenderer = new MeshRenderer(editorCamera, openGLObjects);
        meshRenderers.add(meshRenderer);
        return meshRenderer;
    }

    public void removeInstant(MeshRenderer meshRenderer) {
        meshRenderers.remove(meshRenderer);
    }

    public void renderers() {
        meshRenderers.forEach(MeshRenderer::renderer);
    }
}
