package app.renderer.draw;

import app.math.components.Camera;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;
import app.renderer.debug.grid.Grid;
import app.renderer.framebuffer.Framebuffer;
import app.renderer.ibl.SkyBox;
import app.renderer.lights.LightHandler;
import app.renderer.pbr.MeshRendererHandler;
import app.renderer.terrain.TerrainQuadtreeRenderer;

import static org.lwjgl.opengl.GL11.*;

public class EditorRenderer {
    private static Framebuffer framebuffer;
    private static Camera editorCamera;
    private static Textures textures;
    private static OpenGLObjects openGLObjects;

    private static int fboID;

    private static LightHandler lightHandler;
    private static SkyBox skyBox;
    private static MeshRendererHandler meshRenderer;
    private static Grid grid;

    private static TerrainQuadtreeRenderer terrainQuadtreeRenderer;

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

        terrainQuadtreeRenderer = new TerrainQuadtreeRenderer(openGLObjects, textures, editorCamera, skyBox);

        lightHandler = new LightHandler();
        meshRenderer = new MeshRendererHandler(editorCamera, textures, openGLObjects, skyBox, lightHandler);
    }

    public static void draw() {
        framebuffer.bind(fboID);
        glClearColor(0f, 0f, 0.5f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        enable();
        meshRenderer.renderers();
        terrainQuadtreeRenderer.render();
        lightHandler.drawBillboards(editorCamera);
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

    public static LightHandler getLightHandler() {
        return lightHandler;
    }

    public static OpenGLObjects getOpenGLObjects() {
        return openGLObjects;
    }

    public static TerrainQuadtreeRenderer getTerrainQuadtreeRenderer() {
        return terrainQuadtreeRenderer;
    }
}