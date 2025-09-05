package app.renderer.terrain;

import app.math.OLVector3f;
import app.math.components.Camera;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;
import app.renderer.fog.Fog;
import app.renderer.ibl.SkyBox;
import app.renderer.shaders.UniformsNames;
import app.renderer.terrain.sculpting.TerrainDataManager;
import app.utilities.logger.LogInfo;

import java.nio.FloatBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL40.GL_PATCH_VERTICES;
import static org.lwjgl.opengl.GL40.glPatchParameteri;

public class TerrainQuadtreeRenderer {

    private final TerrainQuadtree terrainQuadtree;
    private final ShaderTerrainQuadtree shaderTerrainQuadtree;
    private final Textures textures;
    private final Camera camera;

    private static final float[] quadData = {
            // Vert 1
            -1.0f, 0.0f, -1.0f, 1.0f,    // Position
            // Vert 2
            1.0f, 0.0f, -1.0f, 1.0f,        // Position
            // Vert 3
            1.0f, 0.0f, 1.0f, 1.0f,        // Position
            // Vert 4
            -1.0f, 0.0f, 1.0f, 1.0f,        // Position
    };

    private static final int[] quadPatchInd = {0, 1, 2, 3};

    private final int vao;
    private int texture;

    private boolean wireframe;
    private boolean isActive;

    private float displacementFactor;

    private final TerrainMaterial terrainMaterial;
    private TerrainDataManager terrainDataManager;

    private Fog fog;
    private final SkyBox skyBox;

    private static final int WIDTH = 8192;
    private static final int LENGTH = 8192;

    public TerrainQuadtreeRenderer(OpenGLObjects openGLObjects, Textures textures, Camera camera, SkyBox skyBox) {

        this.textures = textures;
        shaderTerrainQuadtree = new ShaderTerrainQuadtree(Paths.get("src\\main\\resources\\shaders\\terrain\\quadtree.glsl"));
        shaderTerrainQuadtree.bindBlockBuffer(UniformsNames.MATRICES.getUniformsName(), 0);
        terrainQuadtree = new TerrainQuadtree(camera, shaderTerrainQuadtree);
        vao = openGLObjects.loadToVAO(quadData, quadPatchInd);

        terrainMaterial=new TerrainMaterial(textures);
        wireframe = false;
        displacementFactor = 200f;

        this.camera = camera;
        this.skyBox = skyBox;
    }

    public void init(Path path) {
        texture = textures.loadTexture(path);
        
        // TerrainDataManager will be initialized on-demand by enableSculpting()
        LogInfo.println("Terrain renderer initialized - sculpting will be enabled on demand");

        shaderTerrainQuadtree.start();
        shaderTerrainQuadtree.loadTexHighMap();
        shaderTerrainQuadtree.loadTexModificationMap();
        shaderTerrainQuadtree.loadTerrainWidth(WIDTH);
        shaderTerrainQuadtree.loadTerrainLength(LENGTH);
        OLVector3f origin = new OLVector3f(WIDTH / 2.0f, 0.0f, LENGTH / 2.0f);
        shaderTerrainQuadtree.loadTerrainOrigin(origin);
        shaderTerrainQuadtree.stop();

    }

    public void render() {
        if (isActive) {
            glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);
            shaderTerrainQuadtree.start();
            shaderTerrainQuadtree.loadViewPort(camera.getViewPort());
            shaderTerrainQuadtree.loadCameraPosition(camera.getPosition());

            shaderTerrainQuadtree.loadToggleWireframe(wireframe);
            shaderTerrainQuadtree.loadTerrainHeightOffset(displacementFactor);
            System.out.println("📏 TerrainHeightOffset (displacement factor): " + displacementFactor);
            
            // CRITICAL FIX: Load modification texture uniform
            shaderTerrainQuadtree.loadTexModificationMap();
            System.out.println("🎯 Loaded TexTerrainModification uniform to texture unit 1");


            if (fog != null) {
                shaderTerrainQuadtree.loadIsFog(true);
                shaderTerrainQuadtree.loadFogColor(fog.getFogColor());
                shaderTerrainQuadtree.loadSightRange(fog.getSightRange());
            } else
                shaderTerrainQuadtree.loadIsFog(false);

            glPatchParameteri(GL_PATCH_VERTICES, 4);

            glBindVertexArray(vao);
            glEnableVertexAttribArray(0);

            // Bind base heightmap
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture);
            
            // Bind modification texture if sculpting is enabled
            if (terrainDataManager != null) {
                System.out.println("🎨 Binding modification texture - TerrainDataManager exists");
                terrainDataManager.updateModificationTexture();
                glActiveTexture(GL_TEXTURE1);
                int modTexture = terrainDataManager.getModificationTexture();
                glBindTexture(GL_TEXTURE_2D, modTexture);
                System.out.println("🖼️ Bound modification texture ID: " + modTexture + " to GL_TEXTURE1");
            } else {
                System.out.println("❌ No TerrainDataManager - sculpting not enabled");
            }

            glActiveTexture(GL_TEXTURE2);
            glBindTexture(GL_TEXTURE_CUBE_MAP, skyBox.getIrradianceMap());

            glActiveTexture(GL_TEXTURE3);
            glBindTexture(GL_TEXTURE_2D, terrainMaterial.getAlbedoMap());

            glActiveTexture(GL_TEXTURE4);
            glBindTexture(GL_TEXTURE_2D, terrainMaterial.getNormalMap());

            terrainQuadtree.terrainCreateTree(0, 0, 0, WIDTH, LENGTH);
            terrainQuadtree.terrainRender();

            glDisableVertexAttribArray(0);
            glBindVertexArray(0);

            shaderTerrainQuadtree.stop();
            glDisable(GL_CULL_FACE);
        }

    }

    public float getDisplacementFactor() {
        return displacementFactor;
    }

    public void setDisplacementFactor(float displacementFactor) {
        this.displacementFactor = displacementFactor;
    }

    public void setWireframe(boolean wireframe) {
        this.wireframe = wireframe;
    }

    public float getRenderDepth() {
        return terrainQuadtree.getRenderDepth();
    }

    public float getNumTerrainNodes() {
        return terrainQuadtree.getNumTerrainNodes();
    }

    public void setFog(Fog fog) {
        this.fog = fog;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public TerrainMaterial getTerrainMaterial() {
        return terrainMaterial;
    }
    
    public TerrainDataManager getTerrainDataManager() {
        return terrainDataManager;
    }
    
    public void enableSculpting() {
        if (terrainDataManager == null) {
            // Use reasonable texture resolution (1024x1024) with correct world scale
            int textureResolution = 1024;
            terrainDataManager = new TerrainDataManager(textureResolution, textureResolution, WIDTH);
            
            // Set base heightmap texture for sculpting reference
            if (texture != 0) {
                terrainDataManager.setBaseHeightmap(texture, null);
            }
            
            LogInfo.println("Terrain sculpting enabled - Texture: " + textureResolution + "x" + textureResolution + ", Scale: " + WIDTH);
        }
    }
    
    public void disableSculpting() {
        if (terrainDataManager != null) {
            terrainDataManager.clearModifications();
            LogInfo.println("Terrain sculpting disabled");
        }
    }
    
    public void cleanUp() {
        if (terrainDataManager != null) {
            terrainDataManager.cleanUp();
        }
    }
}
