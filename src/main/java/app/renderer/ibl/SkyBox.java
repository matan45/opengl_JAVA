package app.renderer.ibl;

import app.math.OLMatrix4f;
import app.math.OLVector3f;
import app.math.components.Camera;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;
import app.renderer.framebuffer.Framebuffer;
import app.utilities.data.structures.Pair;

import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS;

public class SkyBox {
    private final ShaderCubeMap backgroundShader;
    private final ShaderIrradiance equiangularToCubeShader;
    private final ShaderIrradianceConvolution irradianceShader;
    private final Shaderbrdf shaderbrdf;
    private final ShaderPreFilter shaderPreFilter;

    private String path;

    private final Camera editorCamera;
    private final Textures textures;
    private final Framebuffer framebuffer;

    private final int captureFBO;
    private final int captureRBO;

    private float exposure;

    private boolean isActive;
    private boolean showLightMap;
    private boolean showPreFilterMap;

    private int envCubeMap;
    private int irradianceMap;
    private int prefilterMap;
    private int brdfLUTTexture;

    private final int quadVAO;
    private static final float[] quadVertices = {
            // positions
            -1.0f, 1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
    };
    private static final float[] quadTextureCoords = {
            // positions
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
    };

    private final int cubeVAO;
    private static final float[] cubeVertices = {
            -1.0f, 1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,

            -1.0f, -1.0f, 1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,

            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,

            -1.0f, -1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,

            -1.0f, 1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, -1.0f,

            -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f
    };

    private static final float[] cubeNormals = {
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,

            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,

            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,

            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,

            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,

            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,

    };
    private static final float[] cubeTextureCoords = {
            0.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,

            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,

            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,

            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,

            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,

            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            0.0f, 0.0f
    };

    public SkyBox(Camera editorCamera, Textures textures, Framebuffer framebuffer, OpenGLObjects openGLObjects) {
        this.editorCamera = editorCamera;
        this.textures = textures;
        this.framebuffer = framebuffer;
        exposure = 1.0f;

        backgroundShader = new ShaderCubeMap(Paths.get("src\\main\\resources\\shaders\\skybox\\background.glsl"));
        equiangularToCubeShader = new ShaderIrradiance(Paths.get("src\\main\\resources\\shaders\\skybox\\cubmap.glsl"));
        irradianceShader = new ShaderIrradianceConvolution(Paths.get("src\\main\\resources\\shaders\\skybox\\equirectangular_convolution.glsl"));
        shaderbrdf = new Shaderbrdf(Paths.get("src\\main\\resources\\shaders\\skybox\\brdf.glsl"));
        shaderPreFilter = new ShaderPreFilter(Paths.get("src\\main\\resources\\shaders\\skybox\\prefilter.glsl"));

        cubeVAO = openGLObjects.loadToVAO(cubeVertices, cubeTextureCoords, cubeNormals);
        quadVAO = openGLObjects.loadToVAO(quadVertices, quadTextureCoords);
        Pair<Integer, Integer> temp = framebuffer.frameBufferFixSize(512, 512);
        captureFBO = temp.getValue();
        captureRBO = temp.getValue2();

    }

    public void init(String filePath) {
        isActive = true;

        cubeMap(filePath);
    }

    private void cubeMap(String filePath) {

        backgroundShader.start();
        backgroundShader.connectTextureUnits();
        backgroundShader.stop();

        equiangularToCubeShader.start();
        equiangularToCubeShader.connectTextureUnits();
        equiangularToCubeShader.stop();

        irradianceShader.start();
        irradianceShader.connectTextureUnits();
        irradianceShader.stop();

        shaderPreFilter.start();
        shaderPreFilter.connectTextureUnits();
        shaderPreFilter.stop();

        irradianceMap = 0;
        prefilterMap = 0;
        brdfLUTTexture = 0;
        int hdrTexture = textures.hdr(filePath);
        path = filePath;

        final OLMatrix4f[] captureViews =
                {
                        new OLMatrix4f().lookAt(new OLVector3f(0.0f, 0.0f, 0.0f), new OLVector3f(1.0f, 0.0f, 0.0f), new OLVector3f(0.0f, -1.0f, 0.0f)),
                        new OLMatrix4f().lookAt(new OLVector3f(0.0f, 0.0f, 0.0f), new OLVector3f(-1.0f, 0.0f, 0.0f), new OLVector3f(0.0f, -1.0f, 0.0f)),
                        new OLMatrix4f().lookAt(new OLVector3f(0.0f, 0.0f, 0.0f), new OLVector3f(0.0f, 1.0f, 0.0f), new OLVector3f(0.0f, 0.0f, 1.0f)),
                        new OLMatrix4f().lookAt(new OLVector3f(0.0f, 0.0f, 0.0f), new OLVector3f(0.0f, -1.0f, 0.0f), new OLVector3f(0.0f, 0.0f, -1.0f)),
                        new OLMatrix4f().lookAt(new OLVector3f(0.0f, 0.0f, 0.0f), new OLVector3f(0.0f, 0.0f, 1.0f), new OLVector3f(0.0f, -1.0f, 0.0f)),
                        new OLMatrix4f().lookAt(new OLVector3f(0.0f, 0.0f, 0.0f), new OLVector3f(0.0f, 0.0f, -1.0f), new OLVector3f(0.0f, -1.0f, 0.0f))
                };

        // configure global opengl state
        // -----------------------------
        glEnable(GL_DEPTH_TEST);
        // set depth function to less than AND equal for skybox depth trick.
        glDepthFunc(GL_LEQUAL);
        // enable seamless cubemap sampling for lower mip levels in the pre-filter map.
        glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);

        envCubeMap = textures.createCubTexture(512, 512);
        convert(captureViews, envCubeMap, 512, 512, hdrTexture, equiangularToCubeShader);
        glBindTexture(GL_TEXTURE_CUBE_MAP, envCubeMap);
        glGenerateMipmap(GL_TEXTURE_CUBE_MAP);

        bindFrameBuffer(32, 32);
        irradianceMap = textures.createCubTexture(32, 32);
        convert(captureViews, irradianceMap, 32, 32, envCubeMap, irradianceShader);

        prefilterMap(shaderPreFilter, captureViews);
        glGenerateMipmap(GL_TEXTURE_CUBE_MAP);

        brdfLUTTexture = textures.createBrdfTexture(512, 512);

        // then re-configure capture framebuffer object and render screen-space quad with BRDF shader.
        bindFrameBuffer(512, 512);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, brdfLUTTexture, 0);
        glViewport(0, 0, 512, 512);

        shaderbrdf.start();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        renderQuad();
        shaderbrdf.stop();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        glDisable(GL_DEPTH_TEST);
        glViewport(0, 0, framebuffer.getWidth(), framebuffer.getHeight());
    }

    public void render() {
        if (isActive) {
            backgroundShader.start();
            backgroundShader.connectTextureUnits();
            backgroundShader.loadViewMatrix(editorCamera.getViewMatrix());
            backgroundShader.loadProjectionMatrix(editorCamera.getProjectionMatrix());
            backgroundShader.loadExposure(exposure);
            glActiveTexture(GL_TEXTURE0);

           /* if (showLightMap)
                glBindTexture(GL_TEXTURE_CUBE_MAP, irradianceMap);
            else if (showPreFilterMap)
                glBindTexture(GL_TEXTURE_CUBE_MAP, prefilterMap); // display prefilter map
            else*/
                glBindTexture(GL_TEXTURE_CUBE_MAP, envCubeMap);

            renderCube();
            backgroundShader.stop();

           /* shaderbrdf.start();
            renderQuad();
            shaderbrdf.stop();*/
        }
    }

    private void prefilterMap(ShaderPreFilter shaderPreFilter, OLMatrix4f[] captureViews) {
        prefilterMap = textures.createCubTexture(128, 128);
        shaderPreFilter.start();
        shaderPreFilter.connectTextureUnits();
        shaderPreFilter.loadProjectionMatrix(new OLMatrix4f());
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, envCubeMap);

        glBindFramebuffer(GL_FRAMEBUFFER, captureFBO);
        int maxMipLevels = 5;
        for (int mip = 0; mip < maxMipLevels; ++mip) {
            // reisze framebuffer according to mip-level size.
            int mipWidth = (int) (128 * Math.pow(0.5, mip));
            int mipHeight = (int) (128 * Math.pow(0.5, mip));
            glBindRenderbuffer(GL_RENDERBUFFER, captureRBO);
            glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, mipWidth, mipHeight);
            glViewport(0, 0, mipWidth, mipHeight);

            float roughness = (float) mip / (float) (maxMipLevels - 1);
            shaderPreFilter.loadRoughness(roughness);
            for (int i = 0; i < 6; ++i) {
                shaderPreFilter.loadViewMatrix(captureViews[i]);
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, prefilterMap, mip);

                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                renderCube();
            }
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glActiveTexture(0);
        shaderPreFilter.stop();
    }

    private void bindFrameBuffer(int width, int height) {
        glBindFramebuffer(GL_FRAMEBUFFER, captureFBO);
        glBindRenderbuffer(GL_RENDERBUFFER, captureRBO);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, width, height);
    }

    private void convert(OLMatrix4f[] captureViews, int cubTexture, int width, int height, int texture, CommonShaderSkyBox shader) {
        shader.start();
        shader.connectTextureUnits();
        shader.loadProjectionMatrix(new OLMatrix4f());
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, texture);

        glViewport(0, 0, width, height); // don't forget to configure the viewport to the capture dimensions.
        glBindFramebuffer(GL_FRAMEBUFFER, captureFBO);
        for (int i = 0; i < 6; ++i) {
            shader.loadViewMatrix(captureViews[i]);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, cubTexture, 0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            renderCube();
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glActiveTexture(0);
        shader.stop();
    }

    private void renderQuad() {
        glBindVertexArray(quadVAO);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }

    private void renderCube() {
        // render Cube
        glBindVertexArray(cubeVAO);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glDrawArrays(GL_TRIANGLES, 0, 36);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
    }

    public int getIrradianceMap() {
        return irradianceMap;
    }

    public int getEnvCubeMap() {
        return envCubeMap;
    }

    public int getPrefilterMap() {
        return prefilterMap;
    }

    public int getBrdfLUTTexture() {
        return brdfLUTTexture;
    }

    public String getPath() {
        return path;
    }

    public void setExposure(float exposure) {
        this.exposure = exposure;
    }

    public float getExposure() {
        return exposure;
    }

    public void setActive(boolean active) {
        if (!active) {
            path = "";
            envCubeMap = 0;
            irradianceMap = 0;
            prefilterMap = 0;
        }
        isActive = active;
    }

    public void setShowLightMap(boolean showLightMap) {
        this.showLightMap = showLightMap;
    }

    public void setShowPreFilterMap(boolean showPreFilterMap) {
        this.showPreFilterMap = showPreFilterMap;
    }
}
