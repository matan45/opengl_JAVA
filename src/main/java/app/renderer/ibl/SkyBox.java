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

public class SkyBox {
    ShaderCubeMap backgroundShader;
    ShaderIrradiance equirectangularToCubemapShader;
    ShaderIrradianceConvolution irradianceShader;

    Camera editorCamera;
    Textures textures;
    Framebuffer framebuffer;
    OpenGLObjects openGLObjects;

    int captureFBO;
    int captureRBO;

    int hdrTexture;
    int irradianceMap;
    int envCubeMap;

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

    public void init() {
        backgroundShader = new ShaderCubeMap(Paths.get("src\\main\\resources\\shaders\\skybox\\background.glsl"));
        equirectangularToCubemapShader = new ShaderIrradiance(Paths.get("src\\main\\resources\\shaders\\skybox\\cubmap.glsl"));
        irradianceShader = new ShaderIrradianceConvolution(Paths.get("src\\main\\resources\\shaders\\skybox\\equirectangular_convolution.glsl"));

        cubeVAO = openGLObjects.loadToVAO(vertices);
        Pair<Integer, Integer> temp = framebuffer.frameBufferFixSize(512, 512);
        captureFBO = temp.getValue();
        captureRBO = temp.getValue2();
        hdrTexture = textures.hdr("C:\\matan\\test\\HDR_029_Sky_Cloudy_Ref.hdr");
        cubemap();
    }

    private void cubemap() {
        OLMatrix4f captureViews[] =
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

        convert(captureViews, envCubeMap, 512, 512, hdrTexture, equirectangularToCubemapShader);

        glBindFramebuffer(GL_FRAMEBUFFER, captureFBO);
        glBindRenderbuffer(GL_RENDERBUFFER, captureRBO);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, 32, 32);

        irradianceMap = textures.createCubTexture(32, 32);

        convert(captureViews, irradianceMap, 32, 32, envCubeMap, irradianceShader);

        glDisable(GL_DEPTH_TEST);
        glViewport(0, 0, framebuffer.getWidth(), framebuffer.getHeight());
    }


    private void convert(OLMatrix4f[] captureViews, int cubTexture, int width, int heigh, int texture, CommonShaderSkyBox shader) {
        shader.start();
        shader.connectTextureUnits();
        shader.loadProjectionMatrix(editorCamera.getProjectionMatrix());
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, texture);

        glViewport(0, 0, width, heigh); // don't forget to configure the viewport to the capture dimensions.
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
        backgroundShader.start();
        backgroundShader.loadViewMatrix(editorCamera.getViewMatrix());
        backgroundShader.loadProjectionMatrix(editorCamera.getProjectionMatrix());
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, envCubeMap);
        //glBindTexture(GL_TEXTURE_CUBE_MAP, irradianceMap);
        renderCube();
        glActiveTexture(0);
        backgroundShader.stop();

    }

}
