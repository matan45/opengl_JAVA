package app.utilities.resource;

import app.renderer.pbr.Mesh;
import app.renderer.shaders.ShaderModel;
import app.renderer.texture.Image;
import org.lwjgl.stb.STBVorbisInfo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Path;
import java.util.List;

public class ResourceManager {
    private static final ResourceWindowGLFW windowGLFW = new ResourceWindowGLFW();
    private static final ResourceShader resourceShader = new ResourceShader();
    private static final ResourceImgui resourceImgui = new ResourceImgui();
    private static final ResourceMesh resourceMesh = new ResourceMesh();
    private static final ResourceAudio resourceAudio = new ResourceAudio();
    private static final ResourceTexture resourceTexture=new ResourceTexture();

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
        return ResourceUtilises.ioResourceToByteBuffer(path.toAbsolutePath().toString());
    }

    public static Mesh loadMeshFromFile(Path path) {
        return resourceMesh.readMeshFile(path);
    }

    public static Mesh[] loadMeshesFromFile(Path path) {
        return resourceMesh.importMeshesFile(path);
    }

    public static Image loadTextureFromFile(Path path) {
        return resourceTexture.importTextureFile(path);
    }

    public static ShortBuffer loadAudio(Path path, STBVorbisInfo info) {
        return resourceAudio.loadSourceFromFile(path, info);
    }
}
