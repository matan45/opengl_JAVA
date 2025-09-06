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
import app.utilities.debug.TerrainDebug;

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
    private Path heightmapPath;

    private static final int WIDTH = 2048;
    private static final int LENGTH = 2048;

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
        LogInfo.println("🔍 Terrain init() called with path: " + path);
        texture = textures.loadTexture(path);
        LogInfo.println("   Loaded texture ID: " + texture);
        
        // Store the heightmap path for later use when sculpting is enabled
        this.heightmapPath = path;
        LogInfo.println("   Stored heightmapPath: " + this.heightmapPath);
        
        // TerrainDataManager will be initialized on-demand by enableSculpting()
        LogInfo.println("✅ Terrain renderer initialized - sculpting will be enabled on demand");

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
            // TerrainDebug.printf("TerrainHeightOffset (displacement factor): %.1f", displacementFactor);
            
            // CRITICAL FIX: Load modification texture uniform
            shaderTerrainQuadtree.loadTexModificationMap();
            // TerrainDebug.println("Loaded TexTerrainModification uniform to texture unit 1");


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
                // TerrainDebug.println("Binding modification texture - TerrainDataManager exists");
                terrainDataManager.updateModificationTexture();
                glActiveTexture(GL_TEXTURE1);
                int modTexture = terrainDataManager.getModificationTexture();
                glBindTexture(GL_TEXTURE_2D, modTexture);
                // TerrainDebug.printf("Bound modification texture ID: %d to GL_TEXTURE1", modTexture);
            } else {
                TerrainDebug.println("❌ No TerrainDataManager - sculpting not enabled");
            }

            glActiveTexture(GL_TEXTURE2);
            glBindTexture(GL_TEXTURE_CUBE_MAP, skyBox.getIrradianceMap());

            glActiveTexture(GL_TEXTURE3);
            glBindTexture(GL_TEXTURE_2D, terrainMaterial.getAlbedoMap());

            glActiveTexture(GL_TEXTURE4);
            glBindTexture(GL_TEXTURE_2D, terrainMaterial.getNormalMap());

            terrainQuadtree.terrainCreateTree(0, 0, 0, WIDTH, LENGTH);
            
            // Quadtree debug info disabled to reduce log noise
            // TerrainDebug.printSeparator("QUADTREE DEBUG INFO");
            // TerrainDebug.printf("Terrain dimensions: %d x %d", WIDTH, LENGTH);
            // TerrainDebug.printf("Quadtree render depth: %d", terrainQuadtree.getRenderDepth());
            // TerrainDebug.printf("Number of terrain nodes: %d", terrainQuadtree.getNumTerrainNodes());
            // TerrainDebug.printf("Camera position: %s", camera.getPosition().toString());
            // TerrainDebug.printf("Wireframe mode: %s", wireframe ? "enabled" : "disabled");
            
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
    
    private FloatBuffer loadHeightmapData(Path heightmapPath, int resolution) {
        try {
            // Use STB to load the heightmap image
            org.lwjgl.system.MemoryStack stack = org.lwjgl.system.MemoryStack.stackPush();
            java.nio.IntBuffer width = stack.mallocInt(1);
            java.nio.IntBuffer height = stack.mallocInt(1);
            java.nio.IntBuffer channels = stack.mallocInt(1);
            
            // Load image data
            java.nio.ByteBuffer imageData = org.lwjgl.stb.STBImage.stbi_load(
                heightmapPath.toString(), width, height, channels, 1);
            
            if (imageData == null) {
                LogInfo.println("❌ Failed to load heightmap: " + heightmapPath);
                return null;
            }
            
            int imageWidth = width.get(0);
            int imageHeight = height.get(0);
            
            LogInfo.println("Loaded heightmap: " + imageWidth + "x" + imageHeight + 
                          " -> " + resolution + "x" + resolution);
            
            // Create FloatBuffer with the desired resolution
            FloatBuffer heightBuffer = org.lwjgl.BufferUtils.createFloatBuffer(resolution * resolution);
            
            // Sample the image data to fit the resolution
            for (int y = 0; y < resolution; y++) {
                for (int x = 0; x < resolution; x++) {
                    // Map resolution coordinates to image coordinates
                    int imgX = (x * imageWidth) / resolution;
                    int imgY = (y * imageHeight) / resolution;
                    
                    // Get pixel value (0-255) and convert to float (0.0-1.0)
                    int pixelIndex = imgY * imageWidth + imgX;
                    if (pixelIndex < imageData.capacity()) {
                        float heightValue = (imageData.get(pixelIndex) & 0xFF) / 255.0f;
                        heightBuffer.put(heightValue);
                    } else {
                        heightBuffer.put(0.0f);
                    }
                }
            }
            
            heightBuffer.flip();
            
            // Free the image data
            org.lwjgl.stb.STBImage.stbi_image_free(imageData);
            stack.pop();
            
            LogInfo.println("✅ Heightmap data loaded: " + heightBuffer.capacity() + " values");
            return heightBuffer;
            
        } catch (Exception e) {
            LogInfo.println("❌ Error loading heightmap data: " + e.getMessage());
            return null;
        }
    }
    
    public void enableSculpting() {
        LogInfo.println("🔍 enableSculpting() called");
        LogInfo.println("   terrainDataManager: " + (terrainDataManager == null ? "null" : "exists"));
        
        if (terrainDataManager == null) {
            LogInfo.println("🔧 Creating new TerrainDataManager");

            // Use full terrain resolution (2048x2048) to match quadtree scale
            int textureResolution = 2048;
            terrainDataManager = new TerrainDataManager(textureResolution, textureResolution, WIDTH);
            
            LogInfo.println("   texture ID: " + texture);
            LogInfo.println("   heightmapPath: " + (heightmapPath != null ? heightmapPath.toString() : "null"));
            
            // Set base heightmap texture and data for sculpting reference
            if (texture != 0 && heightmapPath != null) {
                LogInfo.println("🔍 Loading heightmap data from: " + heightmapPath);
                FloatBuffer heightData = loadHeightmapData(heightmapPath, textureResolution);
                terrainDataManager.setBaseHeightmap(texture, heightData);
                LogInfo.println("✅ Loaded heightmap data: " + (heightData != null ? heightData.capacity() + " values" : "null"));
            } else {
                LogInfo.println("❌ Cannot load heightmap - texture: " + texture + ", path: " + heightmapPath);
            }
            
            LogInfo.println("✅ Terrain sculpting enabled - Texture: " + textureResolution + "x" + textureResolution + ", Scale: " + WIDTH);
        } else {
            LogInfo.println("⚠️ TerrainDataManager already exists - sculpting already enabled");
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
