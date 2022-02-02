package app.renderer.ibl;

import app.math.OLMatrix4f;
import app.math.OLVector3f;
import app.math.components.Camera;
import app.utilities.resource.ResourceManager;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;

public class SkyBox {
    ShaderCubeMap shaderCubeMap;
    ShaderIrradiance shaderIrradiance;
    Camera editorCamera;

    int captureFBO;
    int hdrTexture;

    int cubeVAO = 0;
    int captureRBO;
    int irradianceMap;

    int envCubemap;

    public SkyBox(Camera editorCamera) {
        this.editorCamera = editorCamera;
    }

    public void init() {
        shaderCubeMap = new ShaderCubeMap(Paths.get("src\\main\\resources\\shaders\\skybox\\background.glsl"));
        shaderIrradiance = new ShaderIrradiance(Paths.get("src\\main\\resources\\shaders\\skybox\\cubmap.glsl"));
        // configure global opengl state
        // -----------------------------
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL); // set depth function to less than AND equal for skybox depth trick.

        shaderCubeMap.start();
        shaderCubeMap.connectTextureUnits();
        framebuffer();
        loadHDR();
        cubemap();
    }

    private void framebuffer() {
        captureFBO = glGenFramebuffers();
        captureRBO = glGenRenderbuffers();

        glBindFramebuffer(GL_FRAMEBUFFER, captureFBO);
        glBindRenderbuffer(GL_RENDERBUFFER, captureRBO);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, 512, 512);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, captureRBO);

    }

    private void loadHDR() {
        stbi_set_flip_vertically_on_load(true);
        String pathfile = "C:\\matan\\test\\HDR_029_Sky_Cloudy_Ref.hdr";

        ByteBuffer imageBuffer;
        FloatBuffer image;
        try {
            imageBuffer = ResourceManager.readToByte(Paths.get(pathfile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer comp = BufferUtils.createIntBuffer(1);

        // Use info to read image metadata without decoding the entire image.
        // We don't need this for this demo, just testing the API.
        if (!stbi_info_from_memory(imageBuffer, w, h, comp)) {
            throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
        }

        // Decode the image
        image = stbi_loadf_from_memory(imageBuffer, w, h, comp, 0);
        if (image == null) {
            throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
        }

        hdrTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, hdrTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB16F, w.get(0), h.get(0), 0, GL_RGB, GL_FLOAT, image); // note how we specify the texture's data value to be float

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        stbi_set_flip_vertically_on_load(false);
        stbi_image_free(image);
    }

    private void cubemap() {
        // pbr: setup cubemap to render to and attach to framebuffer
        // ---------------------------------------------------------
        envCubemap = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, envCubemap);
        for (int i = 0; i < 6; ++i) {
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB16F, 512, 512, 0, GL_RGB, GL_FLOAT, 0);
        }
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // pbr: convert HDR equirectangular environment map to cubemap equivalent
        // ----------------------------------------------------------------------
        shaderIrradiance.start();
        shaderIrradiance.connectTextureUnits();
        shaderIrradiance.loadProjectionMatrix(editorCamera.getProjectionMatrix());
        OLMatrix4f captureViews[] =
                {
                        new OLMatrix4f().lookAt(new OLVector3f(0.0f, 0.0f, 0.0f), new OLVector3f(1.0f, 0.0f, 0.0f), new OLVector3f(0.0f, -1.0f, 0.0f)),
                        new OLMatrix4f().lookAt(new OLVector3f(0.0f, 0.0f, 0.0f), new OLVector3f(-1.0f, 0.0f, 0.0f), new OLVector3f(0.0f, -1.0f, 0.0f)),
                        new OLMatrix4f().lookAt(new OLVector3f(0.0f, 0.0f, 0.0f), new OLVector3f(0.0f, 1.0f, 0.0f), new OLVector3f(0.0f, 0.0f, 1.0f)),
                        new OLMatrix4f().lookAt(new OLVector3f(0.0f, 0.0f, 0.0f), new OLVector3f(0.0f, -1.0f, 0.0f), new OLVector3f(0.0f, 0.0f, -1.0f)),
                        new OLMatrix4f().lookAt(new OLVector3f(0.0f, 0.0f, 0.0f), new OLVector3f(0.0f, 0.0f, 1.0f), new OLVector3f(0.0f, -1.0f, 0.0f)),
                        new OLMatrix4f().lookAt(new OLVector3f(0.0f, 0.0f, 0.0f), new OLVector3f(0.0f, 0.0f, -1.0f), new OLVector3f(0.0f, -1.0f, 0.0f))
                };

        glViewport(0, 0, 512, 512); // don't forget to configure the viewport to the capture dimensions.
        glBindFramebuffer(GL_FRAMEBUFFER, captureFBO);
        for (int i = 0; i < 6; ++i) {
            shaderIrradiance.loadViewMatrix(captureViews[i]);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, envCubemap, 0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            renderCube();
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        shaderIrradiance.stop();

        irradianceMap = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, irradianceMap);
        for (int i = 0; i < 6; ++i) {
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB16F, 32, 32, 0, GL_RGB, GL_FLOAT, 0);
        }
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glBindFramebuffer(GL_FRAMEBUFFER, captureFBO);
        glBindRenderbuffer(GL_RENDERBUFFER, captureRBO);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, 32, 32);

        shaderIrradiance.start();
        shaderIrradiance.connectTextureUnits();
        shaderIrradiance.loadProjectionMatrix(editorCamera.getProjectionMatrix());
        glViewport(0, 0, 32, 32); // don't forget to configure the viewport to the capture dimensions.
        glBindFramebuffer(GL_FRAMEBUFFER, captureFBO);
        for (int i = 0; i < 6; ++i) {
            shaderIrradiance.loadViewMatrix(captureViews[i]);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, irradianceMap, 0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            renderCube();
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        shaderIrradiance.stop();
        glViewport(0, 0, 1920, 1080);
    }

    private void renderCube() {
        // initialize (if necessary)
        if (cubeVAO == 0) {
            final float SIZE = 1f;

            final float[] vertices = {
                    -SIZE, SIZE, -SIZE,
                    -SIZE, -SIZE, -SIZE,
                    SIZE, -SIZE, -SIZE,
                    SIZE, -SIZE, -SIZE,
                    SIZE, SIZE, -SIZE,
                    -SIZE, SIZE, -SIZE,

                    -SIZE, -SIZE, SIZE,
                    -SIZE, -SIZE, -SIZE,
                    -SIZE, SIZE, -SIZE,
                    -SIZE, SIZE, -SIZE,
                    -SIZE, SIZE, SIZE,
                    -SIZE, -SIZE, SIZE,

                    SIZE, -SIZE, -SIZE,
                    SIZE, -SIZE, SIZE,
                    SIZE, SIZE, SIZE,
                    SIZE, SIZE, SIZE,
                    SIZE, SIZE, -SIZE,
                    SIZE, -SIZE, -SIZE,

                    -SIZE, -SIZE, SIZE,
                    -SIZE, SIZE, SIZE,
                    SIZE, SIZE, SIZE,
                    SIZE, SIZE, SIZE,
                    SIZE, -SIZE, SIZE,
                    -SIZE, -SIZE, SIZE,

                    -SIZE, SIZE, -SIZE,
                    SIZE, SIZE, -SIZE,
                    SIZE, SIZE, SIZE,
                    SIZE, SIZE, SIZE,
                    -SIZE, SIZE, SIZE,
                    -SIZE, SIZE, -SIZE,

                    -SIZE, -SIZE, -SIZE,
                    -SIZE, -SIZE, SIZE,
                    SIZE, -SIZE, -SIZE,
                    SIZE, -SIZE, -SIZE,
                    -SIZE, -SIZE, SIZE,
                    SIZE, -SIZE, SIZE
            };

            FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
            buffer.put(vertices);
            buffer.flip();

            cubeVAO = glGenVertexArrays();
            int cubeVBO = glGenBuffers();

            // fill buffer
            glBindBuffer(GL_ARRAY_BUFFER, cubeVBO);
            glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
            // link vertex attributes
            glBindVertexArray(cubeVAO);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        }
        // render Cube
        glBindVertexArray(cubeVAO);
        glDrawArrays(GL_TRIANGLES, 0, 36);
        glBindVertexArray(0);
    }

    public void render() {
        // render skybox (render as last to prevent overdraw)
        shaderCubeMap.start();
        shaderCubeMap.loadViewMatrix(editorCamera.getViewMatrix());
        shaderCubeMap.loadProjectionMatrix(editorCamera.getProjectionMatrix());
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, envCubemap);
        //glBindTexture(GL_TEXTURE_CUBE_MAP, irradianceMap);
        renderCube();
        shaderCubeMap.stop();

    }

}
