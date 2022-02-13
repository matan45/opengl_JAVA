package app.renderer.draw;

import app.math.components.Camera;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;
import app.renderer.debug.Grid;
import app.renderer.framebuffer.Framebuffer;
import app.renderer.ibl.SkyBox;

import static org.lwjgl.opengl.GL11.*;

public class EditorRenderer {
    static Framebuffer framebuffer;
    static Camera editorCamera;
    static Textures textures;
    static OpenGLObjects openGLObjects;

    static int fboID;

    static SkyBox skyBox;

    static Grid grid;

    private EditorRenderer() {
    }

    public static void init() {
        textures = new Textures();
        editorCamera = new Camera();
        openGLObjects = new OpenGLObjects();
        framebuffer = new Framebuffer(1920, 1080, textures);
        fboID = framebuffer.createFrameRenderBuffer();
        skyBox = new SkyBox(editorCamera, textures, framebuffer, openGLObjects);
        grid = new Grid(openGLObjects, editorCamera);
    }

    public static void draw() {
        framebuffer.bind(fboID);
        glEnable(GL_DEPTH_TEST);
        glClearColor(0f, 0f, 0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        grid.render();
        skyBox.render();
        framebuffer.unbind();
    }

    public static void cleanUp() {
        textures.cleanUp();
        openGLObjects.cleanUp();
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