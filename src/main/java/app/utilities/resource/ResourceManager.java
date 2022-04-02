package app.utilities.resource;

import app.renderer.pbr.Mesh;
import app.renderer.shaders.ShaderModel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.List;

public class ResourceManager {
    //TODO Resource pooling map path and object Map<path,object>
    private static final ResourceWindowGLFW windowGLFW = new ResourceWindowGLFW();
    private static final ResourceShader resourceShader = new ResourceShader();
    private static final ResourceImgui resourceImgui = new ResourceImgui();
    private static final ResourceMesh resourceMesh = new ResourceMesh();
    private static final ResourceAduio resourceAduio = new ResourceAduio();

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

    public static Mesh[] loadMeshFromFile(Path path) {
        return resourceMesh.readMeshFile(path);
    }
}
