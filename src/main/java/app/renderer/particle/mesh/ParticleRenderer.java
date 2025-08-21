package app.renderer.particle.mesh;

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
import java.util.List;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

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
    
    public void renderParticles(List<ParticleMesh> particles, VaoModel model) {
        if (particles.isEmpty() || model == null) {
            return;
        }
        
        particleShader.start();
        particleShader.connectTextureUnits();
        particleShader.loadCameraPosition(camera.getPosition());
        
        if (lightHandler.getDirectionalLight() != null) {
            particleShader.loadDirLight(lightHandler.getDirectionalLight());
        }
        
        particleShader.loadMetallic(particleMaterial.getMetallic());
        particleShader.loadRoughness(particleMaterial.getRoughness());
        particleShader.loadAo(particleMaterial.getAo());
        particleShader.loadEmissive(particleMaterial.getEmissive());
        
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, skyBox.getIrradianceMap());
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_CUBE_MAP, skyBox.getPrefilterMap());
        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, skyBox.getBrdfLUTTexture());
        
        glActiveTexture(GL_TEXTURE3);
        glBindTexture(GL_TEXTURE_2D, particleMaterial.getAlbedoMap());
        glActiveTexture(GL_TEXTURE4);
        glBindTexture(GL_TEXTURE_2D, particleMaterial.getNormalMap());
        
        glBindVertexArray(model.vaoID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        
        for (ParticleMesh particle : particles) {
            particleShader.loadModelMatrix(particle.getModelMatrix());
            glDrawElements(GL_TRIANGLES, model.VertexCount(), GL_UNSIGNED_INT, 0);
        }
        
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
        
        particleShader.stop();
    }
    
    public VaoModel getVaoModel() {
        return vaoModel;
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
    
    public void setSelect(int select) {
        this.select = select;
    }
}
