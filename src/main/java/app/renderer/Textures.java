package app.renderer;

import app.utilities.resource.ResourceManager;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL30.GL_RGB16F;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class Textures {
    List<Integer> texturesID;

    public Textures() {
        texturesID = new ArrayList<>();
    }

    public int loadTexture(String fileName) {
        ByteBuffer imageBuffer;
        ByteBuffer image;
        try {
            imageBuffer = ResourceManager.readToByte(Paths.get(fileName));
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
        image = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
        if (image == null) {
            throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
        }

        int id = glGenTextures();
        texturesID.add(id);

        glBindTexture(GL_TEXTURE_2D, id);


        if (comp.get(0) == 3) {
            if ((w.get(0) & 3) != 0) {
                glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (w.get(0) & 1));
            }
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, w.get(0), h.get(0), 0, GL_RGB,
                    GL_UNSIGNED_BYTE, image);
        } else {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w.get(0), h.get(0), 0, GL_RGBA,
                    GL_UNSIGNED_BYTE, image);
        }
        glGenerateMipmap(GL_TEXTURE_2D);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        stbi_image_free(image);
        return id;
    }

    public int frameBufferTexture(int width, int height) {

        int id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);

        texturesID.add(id);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height,
                0, GL_RGB, GL_UNSIGNED_BYTE, 0);

        return id;
    }

    public int hdr(String fileName) {
        stbi_set_flip_vertically_on_load(true);


        ByteBuffer imageBuffer;
        FloatBuffer image;
        try {
            imageBuffer = ResourceManager.readToByte(Paths.get(fileName));
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

        int hdrTexture = glGenTextures();
        texturesID.add(hdrTexture);

        glBindTexture(GL_TEXTURE_2D, hdrTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB16F, w.get(0), h.get(0), 0, GL_RGB, GL_FLOAT, image); // note how we specify the texture's data value to be float

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        stbi_set_flip_vertically_on_load(false);
        stbi_image_free(image);
        return hdrTexture;
    }

    public int createCubTexture(int width, int height) {
        int envCubeMap = glGenTextures();
        texturesID.add(envCubeMap);
        glBindTexture(GL_TEXTURE_CUBE_MAP, envCubeMap);
        for (int i = 0; i < 6; ++i) {
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB16F, width, height, 0, GL_RGB, GL_FLOAT, 0);
        }
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        return envCubeMap;
    }


    public void cleanUp() {
        for (int texture : texturesID) {
            glDeleteTextures(texture);
        }
    }
}
