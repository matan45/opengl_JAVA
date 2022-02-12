package app.renderer.pbr;

import app.math.OLVector3f;
import app.math.components.Camera;
import app.math.components.OLTransform;
import app.renderer.OpenGLObjects;
import app.renderer.VaoModel;
import app.utilities.resource.ResourceManager;

import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class MeshRenderer {
    ShaderMesh shaderMesh;
    Camera camera;
    OpenGLObjects openGLObjects;


    Mesh[] meshes;
    VaoModel vaoModel;
    OLTransform olTransform;

    public MeshRenderer(Camera camera, OpenGLObjects openGLObjects) {
        this.camera = camera;
        this.openGLObjects = openGLObjects;
        shaderMesh = new ShaderMesh(Paths.get("src\\main\\resources\\shaders\\pbr\\pbrMesh.glsl"));
    }

    public void init(String filePath, OLTransform olTransform) {
        meshes = ResourceManager.loadMeshFromFile(Paths.get(filePath));
        vaoModel = openGLObjects.loadToVAO(meshes[0].vertices(), meshes[0].textures(), meshes[0].normals(), meshes[0].indices());
        this.olTransform = olTransform;
    }

    public void renderer() {
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        shaderMesh.start();
        shaderMesh.loadModelMatrix(olTransform.getModelMatrix());
        shaderMesh.loadViewMatrix(camera.getViewMatrix());
        shaderMesh.loadProjectionMatrix(camera.getProjectionMatrix());

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
}
