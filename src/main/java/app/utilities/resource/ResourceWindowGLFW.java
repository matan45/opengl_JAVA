package app.utilities.resource;

import org.lwjgl.glfw.GLFWImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;

import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memFree;

class ResourceWindowGLFW {

    protected void setIcon(Path path, long window) {
        IntBuffer w = memAllocInt(1);
        IntBuffer h = memAllocInt(1);
        IntBuffer comp = memAllocInt(1);

        // Icons
        ByteBuffer icon16;
        ByteBuffer icon32;
        try {
            icon16 = ResourceUtilises.ioResourceToByteBuffer(path.toAbsolutePath().toString());
            icon32 = ResourceUtilises.ioResourceToByteBuffer(path.toAbsolutePath().toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try (GLFWImage.Buffer icons = GLFWImage.malloc(2)) {
            ByteBuffer pixels16 = stbi_load_from_memory(icon16, w, h, comp, 4);
            assert pixels16 != null;
            icons.position(0).width(w.get(0)).height(h.get(0)).pixels(pixels16);

            ByteBuffer pixels32 = stbi_load_from_memory(icon32, w, h, comp, 4);
            assert pixels32 != null;
            icons.position(1).width(w.get(0)).height(h.get(0)).pixels(pixels32);

            icons.position(0);
            glfwSetWindowIcon(window, icons);

            stbi_image_free(pixels32);
            stbi_image_free(pixels16);
        }


        memFree(comp);
        memFree(h);
        memFree(w);

    }
}
