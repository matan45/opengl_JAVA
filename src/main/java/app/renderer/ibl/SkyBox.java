package app.renderer.ibl;

import app.math.components.Camera;
import app.utilities.resource.ResourceUtilies;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;

public class SkyBox {
    ShaderCubeMap shaderCubeMap;
    ShaderIrradiance shaderIrradiance;
    Camera editorCamera;

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
    }

    private void framebuffer() {
        int captureFBO = glGenFramebuffers();
        int captureRBO = glGenRenderbuffers();

        glBindFramebuffer(GL_FRAMEBUFFER, captureFBO);
        glBindRenderbuffer(GL_RENDERBUFFER, captureRBO);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, 512, 512);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, captureRBO);

    }

    private void loadHDR() {
        stbi_set_flip_vertically_on_load(true);
        String pathfile = "C:\\matan\\test\\Arches_E_PineTree_3k.hdr";

        ByteBuffer path = BufferUtils.createByteBuffer(pathfile.length() + 1);
        path.put(pathfile.getBytes());
        path.rewind();

        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer comp = BufferUtils.createIntBuffer(1);


        // Decode the image
        FloatBuffer image = stbi_loadf(path, w, h, comp, 0);

        System.out.println(w.get(0));
        System.out.println(h.get(0));
        int hdrTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, hdrTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB16F, w.get(0), h.get(0), 0, GL_RGB, GL_FLOAT, image); // note how we specify the texture's data value to be float

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        assert image != null;
        stbi_image_free(image);
    }

    public void render() {
    }

}
