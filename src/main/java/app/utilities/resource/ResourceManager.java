package app.utilities.resource;

import app.renderer.shaders.ShaderModel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.List;

public class ResourceManager {
    //TODO Resource pooling map path and object Map<path,object>
    static ResourceWindowGLFW windowGLFW = new ResourceWindowGLFW();
    static ResourceShader resourceShader = new ResourceShader();
    static ResourceImgui resourceImgui = new ResourceImgui();
    static ResourceMesh resourceMesh = new ResourceMesh();

    private ResourceManager() {
    }

    public static List<ShaderModel> readShader(Path path) {
        return resourceShader.readShaderFile(path);
    }

    public static byte[] loadFromResources(Path path) {
        return resourceImgui.loadAsByte(path);
    }

    public static void setWindowGLFWIcon(Path path, long window) {
        windowGLFW.setIcon(path, window);
    }

    public static ByteBuffer readToByte(Path path) throws IOException {
        return ResourceUtilies.ioResourceToByteBuffer(path.toAbsolutePath().toString());
    }
}
