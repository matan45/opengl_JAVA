package app.renderer.pbr;

import app.math.components.Camera;
import app.math.components.OLTransform;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;
import app.renderer.VaoModel;
import app.renderer.fog.Fog;
import app.renderer.ibl.SkyBox;
import app.renderer.lights.LightHandler;
import app.renderer.shaders.UniformsNames;

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

    private int select;

    private VaoModel vaoModel;
    private OLTransform olTransform;

    private final LightHandler lightHandler;

    private Fog fog;

    public MeshRenderer(Camera camera, OpenGLObjects openGLObjects, Textures textures, SkyBox skyBox, LightHandler lightHandler) {
        this.camera = camera;
        this.openGLObjects = openGLObjects;
        shaderMesh = new ShaderMesh(Paths.get("resources\\shaders\\pbr\\pbrMesh.glsl"));
        shaderMesh.bindBlockBuffer(UniformsNames.MATRICES.getUniformsName(), 0);
        material = new Material(textures);

        this.skyBox = skyBox;
        this.lightHandler = lightHandler;

        shaderMesh.start();
        shaderMesh.connectTextureUnits();
        shaderMesh.stop();
    }

    public void init(Mesh mesh, OLTransform olTransform) {

        vaoModel = openGLObjects.loadToVAO(mesh.vertices(), mesh.textures(), mesh.normals(), mesh.indices());
        this.olTransform = olTransform;
    }

    public void renderer() {
        if (olTransform != null) {
            if (select != 3) {
                glEnable(GL_CULL_FACE);
                if (select == 0)
                    glCullFace(GL_BACK);
                else if (select == 1)
                    glCullFace(GL_FRONT);
                else
                    glCullFace(GL_FRONT_AND_BACK);
            }
            shaderMesh.start();
            shaderMesh.loadModelMatrix(olTransform.getModelMatrix());
            shaderMesh.loadCameraPosition(camera.getPosition());

            shaderMesh.loadDirLight(lightHandler.getDirectionalLight());
            shaderMesh.loadPointLights(lightHandler.getPointLights());
            shaderMesh.loadSpotLights(lightHandler.getSpotLights());

            if (fog != null) {
                shaderMesh.loadIsFog(true);
                shaderMesh.loadFogColor(fog.getFogColor());
                shaderMesh.loadSightRange(fog.getSightRange());
            } else
                shaderMesh.loadIsFog(false);

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
            if (select != 3)
                glDisable(GL_CULL_FACE);
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeshRenderer that = (MeshRenderer) o;
        return Objects.equals(this, that);
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    public Material getMaterial() {
        return material;
    }

    public void setFog(Fog fog) {
        this.fog = fog;
    }

    public void setSelect(int select) {
        this.select = select;
    }

    public int getSelect() {
        return select;
    }
}
