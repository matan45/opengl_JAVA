package app.renderer.pbr;

import app.math.components.Camera;
import app.math.components.OLTransform;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;
import app.renderer.VaoModel;
import app.renderer.ibl.SkyBox;
import app.utilities.resource.ResourceManager;

import java.nio.file.Paths;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class MeshRenderer {
    private final ShaderMesh shaderMesh;
    private final Camera camera;
    private final OpenGLObjects openGLObjects;
    private final Material material;
    private final SkyBox skyBox;

    private String path;
    private VaoModel vaoModel;
    private OLTransform olTransform;

    public MeshRenderer(Camera camera, OpenGLObjects openGLObjects, Textures textures, SkyBox skyBox) {
        this.camera = camera;
        this.openGLObjects = openGLObjects;
        shaderMesh = new ShaderMesh(Paths.get("src\\main\\resources\\shaders\\pbr\\pbrMesh.glsl"));
        material = new Material(textures);
        this.skyBox = skyBox;

        shaderMesh.start();
        shaderMesh.connectTextureUnits();
        shaderMesh.stop();
    }

    public void init(String filePath, OLTransform olTransform) {
        Mesh[] meshes = ResourceManager.loadMeshFromFile(Paths.get(filePath));
        vaoModel = openGLObjects.loadToVAO(meshes[0].vertices(), meshes[0].textures(), meshes[0].normals(), meshes[0].indices());
        this.olTransform = olTransform;
        path = filePath;
    }

    public void renderer() {
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        shaderMesh.start();
        shaderMesh.loadModelMatrix(olTransform.getModelMatrix());
        shaderMesh.loadViewMatrix(camera.getViewMatrix());
        shaderMesh.loadProjectionMatrix(camera.getProjectionMatrix());
        shaderMesh.loadCameraPosition(camera.getPosition());

        glBindVertexArray(vaoModel.vaoID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glDrawElements(GL_TRIANGLES, vaoModel.VertexCount(), GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);

        shaderMesh.stop();
        glDisable(GL_CULL_FACE);
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeshRenderer that = (MeshRenderer) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    public Material getMaterial() {
        return material;
    }
}
