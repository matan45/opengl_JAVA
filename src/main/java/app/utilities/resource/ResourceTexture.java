package app.utilities.resource;

import app.renderer.texture.Image;
import app.utilities.FileUtil;
import app.utilities.UUIDItem;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class ResourceTexture {
    public Image importTextureFile(Path path) {
        ByteBuffer imageBuffer;
        FloatBuffer image;
        try {
            imageBuffer = ResourceManager.readToByte(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String fileName = FileUtil.getFileNameWithoutExtension(path.toFile());

        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer comp = BufferUtils.createIntBuffer(1);

        // Use info to read image metadata without decoding the entire image.
        // We don't need this for this demo, just testing the API.
        if (!stbi_info_from_memory(imageBuffer, w, h, comp))
            throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());

        // Decode the image
        image = stbi_loadf_from_memory(imageBuffer, w, h, comp, 0);
        if (image == null)
            throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
        int format;
        if (comp.get(0) == 3)
            format = GL_RGB;
        else if (comp.get(0) == 1)
            format = GL_RED;
        else
            format = GL_RGBA;

        float[] floatData = new float[image.capacity()];
        for (int i = 0; i < image.capacity(); i++)
            floatData[i] = image.get(i);

        stbi_image_free(image);
        return new Image(floatData, fileName, w.get(0), h.get(0), format, UUIDItem.generateUUID());
    }
}
