package app.renderer.draw;

import app.math.components.Camera;
import app.renderer.Textures;
import app.renderer.framebuffer.Framebuffer;
import app.renderer.ibl.SkyBox;

import static org.lwjgl.opengl.GL11.*;

public class EditorRenderer {
    static Framebuffer framebuffer;
    static Camera editorCamera;
    static Textures textures;

    static int fboID;

    static SkyBox skyBox;

    private EditorRenderer() {
    }

    public static void init() {
        textures = new Textures();
        editorCamera = new Camera();
        framebuffer = new Framebuffer(1920, 1080, textures);
        fboID = framebuffer.createFrameRenderBuffer();
        skyBox = new SkyBox(editorCamera,textures);
        skyBox.init();
    }

    public static void draw() {
        framebuffer.bind(fboID);
        glClearColor(0.48f, 0.6f, 0.9f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        skyBox.render();
        framebuffer.unbind();
    }

    public static void cleanUp() {
        textures.cleanUp();
    }

    public static Framebuffer getFramebuffer() {
        return framebuffer;
    }

    public static Camera getEditorCamera() {
        return editorCamera;
    }

    public static Textures getTextures() {
        return textures;
    }

    public static int getTexturesID() {return fboID;
    }
}
