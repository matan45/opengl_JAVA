package app.renderer.draw;

import app.math.components.Camera;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;
import app.renderer.framebuffer.Framebuffer;
import app.renderer.ibl.SkyBox;
import app.renderer.pbr.MeshRenderer;
import app.renderer.shaders.ShaderManager;

import static org.lwjgl.opengl.GL11.*;

public class EditorRenderer {
    private static Framebuffer framebuffer;
    private static Camera editorCamera;
    private static Textures textures;
    private static OpenGLObjects openGLObjects;

    private static int fboID;

    private static SkyBox skyBox;
    private static MeshRenderer meshRenderer;

    private EditorRenderer() {
    }

    public static void init() {
        textures = new Textures();
        editorCamera = new Camera();
        openGLObjects = new OpenGLObjects();
        framebuffer = new Framebuffer(1920, 1080, textures);
        fboID = framebuffer.createFrameRenderBuffer();
        skyBox = new SkyBox(editorCamera, textures, framebuffer, openGLObjects);
        meshRenderer = new MeshRenderer(editorCamera, openGLObjects);
        meshRenderer.init("");
    }

    public static void draw() {
        framebuffer.bind(fboID);
        glEnable(GL_DEPTH_TEST);
        glClearColor(0.48f, 0.6f, 0.9f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        meshRenderer.renderer();
        skyBox.render();
        glDisable(GL_DEPTH_TEST);
        framebuffer.unbind();
    }

    public static void cleanUp() {
        textures.cleanUp();
        openGLObjects.cleanUp();
        ShaderManager.cleanUp();
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

    public static int getTexturesID() {
        return fboID;
    }

    public static SkyBox getSkyBox() {
        return skyBox;
    }
}
