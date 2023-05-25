package app.renderer.draw;

import app.audio.Audio;
import app.math.OLMatrix4f;
import app.math.OLVector3f;
import app.math.components.Camera;
import app.math.components.OLTransform;
import app.math.components.RayCast;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;
import app.renderer.debug.grid.Grid;
import app.renderer.framebuffer.Framebuffer;
import app.renderer.ibl.SkyBox;
import app.renderer.lights.LightHandler;
import app.renderer.particle.mesh.ParticleRendererHandler;
import app.renderer.particle.sprite.Particle;
import app.renderer.particle.sprite.ParticleHandler;
import app.renderer.particle.sprite.ParticleRendererSprite;
import app.renderer.pbr.MeshRendererHandler;
import app.renderer.terrain.TerrainQuadtreeRenderer;
import app.utilities.logger.LogInfo;

import java.nio.file.Path;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION;

public class EditorRenderer {
    private static Framebuffer framebuffer;
    private static Camera editorCamera;
    private static Textures textures;
    private static OpenGLObjects openGLObjects;
    private static int fboID;
    private static LightHandler lightHandler;
    private static SkyBox skyBox;
    private static MeshRendererHandler meshRenderer;
    private static ParticleRendererHandler particleRenderer;
    private static Grid grid;
    private static TerrainQuadtreeRenderer terrainQuadtreeRenderer;

    private EditorRenderer() {
    }

    public static void init() {
        LogInfo.println("OPENGL VERSION " + Objects.requireNonNull(glGetString(GL_VERSION)));
        LogInfo.println("GLSL VERSION " + Objects.requireNonNull(glGetString(GL_SHADING_LANGUAGE_VERSION)));
        LogInfo.println("VENDOR " + Objects.requireNonNull(glGetString(GL_VENDOR)));
        LogInfo.println("RENDERER " + Objects.requireNonNull(glGetString(GL_RENDERER)));

        textures = new Textures();
        openGLObjects = new OpenGLObjects();
        editorCamera = new Camera(openGLObjects);
        RayCast.camera = editorCamera;
        framebuffer = new Framebuffer(1920, 1080, textures);
        fboID = framebuffer.createColorAttachmentBuffer();

        skyBox = new SkyBox(textures, framebuffer, openGLObjects);
        grid = new Grid(openGLObjects, framebuffer, editorCamera);

        terrainQuadtreeRenderer = new TerrainQuadtreeRenderer(openGLObjects, textures, editorCamera, skyBox);

        lightHandler = new LightHandler();
        meshRenderer = new MeshRendererHandler(editorCamera, textures, openGLObjects, skyBox, lightHandler);
        particleRenderer = new ParticleRendererHandler(editorCamera, textures, openGLObjects, skyBox, lightHandler);
        ParticleHandler.init(openGLObjects);

        ParticleHandler.setImage(textures.loadTexture(Path.of("C:\\matan\\test\\particle\\circle-256.png")));

        ParticleHandler.create(
                new Particle(new OLVector3f(2.0f, 2.0f, 2.0f), new OLVector3f(),
                        new OLVector3f(5.0f, 5.0f, 5.0f), new OLVector3f(), 1.0f, 10.0f), 50
        );

        ParticleHandler.create(
                new Particle(new OLVector3f(3.0f, 3.0f, 3.0f), new OLVector3f(),
                        new OLVector3f(5.0f, 5.0f, 5.0f), new OLVector3f(), -1.0f, 5.0f), 50
        );

        ParticleHandler.setIsInfinity(true);
    }

    public static void draw(float dt) {
        framebuffer.bind(fboID);
        glClearColor(0f, 0f, 0.5f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        enable();
        Audio.billboards();
        editorCamera.updateMatrices();
        ParticleHandler.update(dt);
        meshRenderer.renderers();
        terrainQuadtreeRenderer.render();
        ParticleHandler.render();
        lightHandler.drawBillboards();
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

    public static ParticleRendererHandler getParticleRenderer() {
        return particleRenderer;
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