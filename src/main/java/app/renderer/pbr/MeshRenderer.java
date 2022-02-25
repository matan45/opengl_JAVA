package app.renderer.pbr;

import app.math.components.Camera;
import app.math.components.OLTransform;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;
import app.renderer.VaoModel;
import app.renderer.ibl.SkyBox;
import app.renderer.lights.DirectionalLight;
import app.renderer.lights.LightHandler;
import app.utilities.resource.ResourceManager;

import java.nio.file.Paths;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
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

    private DirectionalLight directionalLight;

    private final LightHandler lightHandler;

    public MeshRenderer(Camera camera, OpenGLObjects openGLObjects, Textures textures, SkyBox skyBox, DirectionalLight directionalLight, LightHandler lightHandler) {
        this.camera = camera;
        this.openGLObjects = openGLObjects;
        shaderMesh = new ShaderMesh(Paths.get("src\\main\\resources\\shaders\\pbr\\pbrMesh.glsl"));
        material = new Material(textures);

        this.directionalLight = directionalLight;
        this.skyBox = skyBox;
        this.lightHandler = lightHandler;

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

        shaderMesh.loadDirLight(directionalLight);
        shaderMesh.loadPointLights(lightHandler.getPointLights());
        shaderMesh.loadSpotLights(lightHandler.getSpotLights());

        glBindVertexArray(vaoModel.vaoID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        bind();

        glDrawElements(GL_TRIANGLES, vaoModel.VertexCount(), GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);

        shaderMesh.stop();
        glDisable(GL_CULL_FACE);
    }

    private void bind() {

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, skyBox.getIrradianceMap());

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_CUBE_MAP, skyBox.getPrefilterMap());

        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, skyBox.getBrdfLUTTexture());

        glActiveTexture(GL_TEXTURE3);
        glBindTexture(GL_TEXTURE_2D, material.getAlbedoMap());

        glActiveTexture(GL_TEXTURE4);
        glBindTexture(GL_TEXTURE_2D, material.getNormalMap());

        glActiveTexture(GL_TEXTURE5);
        glBindTexture(GL_TEXTURE_2D, material.getMetallicMap());

        glActiveTexture(GL_TEXTURE6);
        glBindTexture(GL_TEXTURE_2D, material.getRoughnessMap());

        glActiveTexture(GL_TEXTURE7);
        glBindTexture(GL_TEXTURE_2D, material.getAoMap());

        glActiveTexture(GL_TEXTURE8);
        glBindTexture(GL_TEXTURE_2D, material.getEmissiveMap());

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

    public void setDirectionalLight(DirectionalLight directionalLight) {
        this.directionalLight = directionalLight;
    }
}
