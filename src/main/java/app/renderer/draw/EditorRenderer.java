package app.renderer.draw;

import app.math.components.Camera;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;
import app.renderer.debug.Grid;
import app.renderer.framebuffer.Framebuffer;
import app.renderer.ibl.SkyBox;
import app.renderer.pbr.MeshRendererHandler;

import static org.lwjgl.opengl.GL11.*;

public class EditorRenderer {
    private static Framebuffer framebuffer;
    private static Camera editorCamera;
    private static Textures textures;
    private static OpenGLObjects openGLObjects;

    private static int fboID;

    private static SkyBox skyBox;
    private static MeshRendererHandler meshRenderer;
    private static Grid grid;

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
        meshRenderer = new MeshRendererHandler(editorCamera, textures, openGLObjects, skyBox);
    }

    public static void draw() {
        framebuffer.bind(fboID);
        glClearColor(0f, 0f, 0.5f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        enable();
        meshRenderer.renderers();
        skyBox.render();
        grid.render();
        disable();
        framebuffer.unbind();
    }

    private static void enable() {
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
    }

    private static void disable() {
        glDisable(GL_DEPTH_TEST);
    }


    public static void cleanUp() {
        textures.cleanUp();
        openGLObjects.cleanUp();
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

    public static Grid getGrid() {
        return grid;
    }

    public static MeshRendererHandler getMeshRenderer() {
        return meshRenderer;
    }
}