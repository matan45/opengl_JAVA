package app.renderer.draw;

import app.audio.Audio;
import app.math.OLVector2f;
import app.math.components.Camera;
import app.math.components.RayCast;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;
import app.renderer.debug.grid.Grid;
import app.renderer.framebuffer.Framebuffer;
import app.renderer.ibl.SkyBox;
import app.renderer.lights.LightHandler;
import app.renderer.particle.mesh.ParticleRendererHandler;
import app.renderer.particle.mesh.ParticleSystemMesh;
import app.renderer.particle.sprite.ParticleSystemSprite;
import app.renderer.pbr.MeshRendererHandler;
import app.renderer.terrain.TerrainQuadtreeRenderer;
import app.renderer.terrain.sculpting.TerrainSculptingIntegration;
import app.ecs.EntitySystem;
import app.utilities.logger.LogInfo;

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
    private static TerrainSculptingIntegration sculptingIntegration;
    private static EntitySystem entitySystem;

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
        ParticleSystemSprite.init(openGLObjects,textures);
        ParticleSystemMesh.init(editorCamera, openGLObjects, textures, skyBox, lightHandler);
        
        // Initialize terrain sculpting integration
        // Note: EntitySystem will be set later when available
        sculptingIntegration = new TerrainSculptingIntegration();
        if (entitySystem != null) {
            sculptingIntegration.initialize(editorCamera, entitySystem);
        }

        /*ParticleEmitter particleEmitter = ParticleSystemSprite.createEmitter();
        particleEmitter.setImage(textures.loadTexture(Path.of("C:\\matan\\test\\particle\\circle-256.png")));*/

      /*  particleEmitter.createParticle(
                new Particle(new OLVector3f(2.0f, 2.0f, 2.0f), new OLVector3f(),
                        new OLVector3f(5.0f, 5.0f, 5.0f), new OLVector3f(), 1.0f, 5.0f), 200
        );

        particleEmitter.createParticle(
                new Particle(new OLVector3f(2.0f, 2.0f, 2.0f), new OLVector3f(),
                        new OLVector3f(5.0f, 5.0f, 5.0f), new OLVector3f(), -1.0f, 5.0f), 200
        );

        particleEmitter.setInfinity(true);
        particleEmitter.setPause(true);
        particleEmitter.setPlay(true);*/
    }

    public static void draw(float dt) {
        framebuffer.bind(fboID);
        glClearColor(0f, 0f, 0.5f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        enable();
        Audio.billboards();
        editorCamera.updateMatrices();
        ParticleSystemSprite.update(dt);
        ParticleSystemMesh.update(dt);
        
        // Update sculpting system before rendering
        if (sculptingIntegration != null && sculptingIntegration.isInitialized()) {
            OLVector2f viewport = editorCamera.getViewPort();
            float viewportWidth = viewport != null ? viewport.x : 1920f;
            float viewportHeight = viewport != null ? (viewport.y - 50f) : 1030f; // Subtract toolbar height
            sculptingIntegration.update(dt, viewportWidth, viewportHeight);
        }
        
        meshRenderer.renderers();
        terrainQuadtreeRenderer.render();
        
        // Render brush preview after terrain but before UI elements
        if (sculptingIntegration != null && sculptingIntegration.isInitialized()) {
            OLVector2f viewport = editorCamera.getViewPort();
            float viewportWidth = viewport != null ? viewport.x : 1920f;
            float viewportHeight = viewport != null ? (viewport.y - 50f) : 1030f; // Subtract toolbar height
            sculptingIntegration.renderBrushPreview(viewportWidth, viewportHeight);
        }
        
        lightHandler.drawBillboards();
        skyBox.render();
        ParticleSystemSprite.render();
        ParticleSystemMesh.render();
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
        ParticleSystemSprite.cleanUp();
        ParticleSystemMesh.cleanUp();
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
    
    public static TerrainSculptingIntegration getSculptingIntegration() {
        return sculptingIntegration;
    }
    
    public static void initializeSculpting() {
        if (sculptingIntegration != null && !sculptingIntegration.isInitialized()) {
            sculptingIntegration.initialize(editorCamera);
        }
    }
}