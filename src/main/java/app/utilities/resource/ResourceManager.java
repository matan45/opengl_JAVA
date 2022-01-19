package app.utilities.resource;

import java.nio.file.Path;

public class ResourceManager {
    static ResourceWindowGLFW windowGLFW = new ResourceWindowGLFW();
    static ResourceShader resourceShader = new ResourceShader();
    static ResourceImgui resourceImgui = new ResourceImgui();

    private ResourceManager() {
    }

    public static StringBuilder readShader(Path path) {
        return resourceShader.readShaderFile(path);
    }

    public static byte[] loadFromResources(Path path) {
        return resourceImgui.loadAsByte(path);
    }

    public static void setWindowGLFWIcon(Path path, long window) {
        windowGLFW.setIcon(path, window);
    }
}
