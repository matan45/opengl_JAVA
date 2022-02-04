package app.renderer.ibl;

import app.math.OLMatrix4f;
import app.math.OLVector3f;
import app.math.components.Camera;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;
import app.renderer.framebuffer.Framebuffer;
import app.utilities.data.structures.Pair;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class SkyBox {
    ShaderCubeMap backgroundShader;

    Camera editorCamera;
    Textures textures;
    Framebuffer framebuffer;
    OpenGLObjects openGLObjects;

    int captureFBO;
    int captureRBO;

    boolean isActive;
    boolean showLightMap;
    boolean showPreFilterMap;

    int irradianceMap;
    int envCubeMap;
    int prefilterMap;

    int cubeVAO;
    private static final float[] vertices = {
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


    public SkyBox(Camera editorCamera, Textures textures, Framebuffer framebuffer, OpenGLObjects openGLObjects) {
        this.editorCamera = editorCamera;
        this.textures = textures;
        this.framebuffer = framebuffer;
        this.openGLObjects = openGLObjects;
    }

    public void init(String filePath) {
        backgroundShader = new ShaderCubeMap(Paths.get("src\\main\\resources\\shaders\\skybox\\background.glsl"));
        isActive = true;
        cubeVAO = openGLObjects.loadToVAO(vertices);
        Pair<Integer, Integer> temp = framebuffer.frameBufferFixSize(512, 512);
        captureFBO = temp.getValue();
        captureRBO = temp.getValue2();
        cubemap(filePath);
    }

    private void cubemap(String filePath) {
        final ShaderIrradiance equiangularToCubeShader = new ShaderIrradiance(Paths.get("src\\main\\resources\\shaders\\skybox\\cubmap.glsl"));
        final ShaderIrradianceConvolution irradianceShader = new ShaderIrradianceConvolution(Paths.get("src\\main\\resources\\shaders\\skybox\\equirectangular_convolution.glsl"));
        final Shaderbrdf shaderbrdf = new Shaderbrdf(Paths.get("src\\main\\resources\\shaders\\skybox\\brdf.glsl"));
        final ShaderPreFilter shaderPreFilter = new ShaderPreFilter(Paths.get("src\\main\\resources\\shaders\\skybox\\prefilter.glsl"));


        final int hdrTexture = textures.hdr(filePath);

        final OLMatrix4f[] captureViews =
                {
                        new OLMatrix4f().lookAt(new OLVector3f(0.0f, 0.0f, 0.0f), new OLVector3f(1.0f, 0.0f, 0.0f), new OLVector3f(0.0f, -1.0f, 0.0f)),
                        new OLMatrix4f().lookAt(new OLVector3f(0.0f, 0.0f, 0.0f), new OLVector3f(-1.0f, 0.0f, 0.0f), new OLVector3f(0.0f, -1.0f, 0.0f)),
                        new OLMatrix4f().lookAt(new OLVector3f(0.0f, 0.0f, 0.0f), new OLVector3f(0.0f, 1.0f, 0.0f), new OLVector3f(0.0f, 0.0f, 1.0f)),
                        new OLMatrix4f().lookAt(new OLVector3f(0.0f, 0.0f, 0.0f), new OLVector3f(0.0f, -1.0f, 0.0f), new OLVector3f(0.0f, 0.0f, -1.0f)),
                        new OLMatrix4f().lookAt(new OLVector3f(0.0f, 0.0f, 0.0f), new OLVector3f(0.0f, 0.0f, 1.0f), new OLVector3f(0.0f, -1.0f, 0.0f)),
                        new OLMatrix4f().lookAt(new OLVector3f(0.0f, 0.0f, 0.0f), new OLVector3f(0.0f, 0.0f, -1.0f), new OLVector3f(0.0f, -1.0f, 0.0f))
                };

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL); // set depth function to less than AND equal for skybox depth trick.

        envCubeMap = textures.createCubTexture(512, 512);
        convert(captureViews, envCubeMap, 512, 512, hdrTexture, equiangularToCubeShader);

        glBindFramebuffer(GL_FRAMEBUFFER, captureFBO);
        glBindRenderbuffer(GL_RENDERBUFFER, captureRBO);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, 32, 32);

        irradianceMap = textures.createCubTexture(32, 32);
        convert(captureViews, irradianceMap, 32, 32, envCubeMap, irradianceShader);

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


        int brdfLUTTexture = glGenTextures();

        // pre-allocate enough memory for the LUT texture.
        glBindTexture(GL_TEXTURE_2D, brdfLUTTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RG16F, 512, 512, 0, GL_RG, GL_FLOAT, 0);
        // be sure to set wrapping mode to GL_CLAMP_TO_EDGE
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // then re-configure capture framebuffer object and render screen-space quad with BRDF shader.
        glBindFramebuffer(GL_FRAMEBUFFER, captureFBO);
        glBindRenderbuffer(GL_RENDERBUFFER, captureRBO);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, 512, 512);
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

    int quadVAO = 0;
    int quadVBO;

    private void renderQuad() {
        if (quadVAO == 0) {
            float[] quadVertices = {
                    // positions        // texture Coords
                    -1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                    -1.0f, -1.0f, 0.0f, 0.0f, 0.0f,
                    1.0f, 1.0f, 0.0f, 1.0f, 1.0f,
                    1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
            };
            FloatBuffer buffer = BufferUtils.createFloatBuffer(quadVertices.length);
            buffer.put(quadVertices);
            buffer.flip();
            // setup plane VAO
            quadVAO = glGenVertexArrays();
            quadVBO = glGenBuffers();
            glBindVertexArray(quadVAO);
            glBindBuffer(GL_ARRAY_BUFFER, quadVBO);
            glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 3);
        }
        glBindVertexArray(quadVAO);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        glBindVertexArray(0);
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

    private void renderCube() {
        // render Cube
        glBindVertexArray(cubeVAO);
        glEnableVertexAttribArray(0);
        glDrawArrays(GL_TRIANGLES, 0, 36);
        glBindVertexArray(0);
    }

    public void render() {
        if (isActive) {
            backgroundShader.start();
            backgroundShader.connectTextureUnits();
            backgroundShader.loadViewMatrix(editorCamera.getViewMatrix());
            backgroundShader.loadProjectionMatrix(editorCamera.getProjectionMatrix());
            glActiveTexture(GL_TEXTURE0);

            if (showLightMap)
                glBindTexture(GL_TEXTURE_CUBE_MAP, irradianceMap);
            else if (showPreFilterMap)
                glBindTexture(GL_TEXTURE_CUBE_MAP, prefilterMap); // display prefilter map
            else
                glBindTexture(GL_TEXTURE_CUBE_MAP, envCubeMap);

            renderCube();
            glActiveTexture(0);
            backgroundShader.stop();
        }
    }

    public int getIrradianceMap() {
        return irradianceMap;
    }

    public int getPrefilterMap() {
        return prefilterMap;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setShowLightMap(boolean showLightMap) {
        this.showLightMap = showLightMap;
    }

    public void setShowPreFilterMap(boolean showPreFilterMap) {
        this.showPreFilterMap = showPreFilterMap;
    }
}
