package app.renderer.terrain.sculpting;

import app.renderer.terrain.TerrainQuadtreeRenderer;
import app.utilities.logger.LogInfo;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.nio.FloatBuffer;
import java.nio.file.Path;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class TerrainDataManager {
    private final int terrainWidth;
    private final int terrainHeight;
    private final float terrainScale;
    
    private int baseHeightmapTexture;
    private int modificationTexture;
    private FloatBuffer baseHeightData;
    private TerrainModificationBuffer modificationBuffer;
    private TerrainActionManager actionManager;
    
    private boolean needsTextureUpdate = false;

    public TerrainDataManager(int width, int height, float scale) {
        this.terrainWidth = width;
        this.terrainHeight = height;
        this.terrainScale = scale;
        
        initializeTextures();
        this.modificationBuffer = new TerrainModificationBuffer(width, height);
        this.actionManager = new TerrainActionManager();
        
        LogInfo.println("TerrainDataManager initialized: " + width + "x" + height);
    }

    public TerrainDataManager(TerrainQuadtreeRenderer terrainRenderer) {
        // Default size for terrain - you may want to get this from the renderer
        this.terrainWidth = 512;
        this.terrainHeight = 512;
        this.terrainScale = 1.0f;
        
        initializeTextures();
        this.modificationBuffer = new TerrainModificationBuffer(terrainWidth, terrainHeight);
        this.actionManager = new TerrainActionManager();
        
        LogInfo.println("TerrainDataManager initialized for terrain renderer: " + terrainWidth + "x" + terrainHeight);
    }

    private void initializeTextures() {
        baseHeightmapTexture = glGenTextures();
        modificationTexture = glGenTextures();
        
        glBindTexture(GL_TEXTURE_2D, modificationTexture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        
        FloatBuffer initialData = BufferUtils.createFloatBuffer(terrainWidth * terrainHeight);
        for (int i = 0; i < terrainWidth * terrainHeight; i++) {
            initialData.put(0.0f);
        }
        initialData.flip();
        
        glTexImage2D(GL_TEXTURE_2D, 0, GL_R32F, terrainWidth, terrainHeight, 0, GL_RED, GL_FLOAT, initialData);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void setBaseHeightmap(int textureId, FloatBuffer heightData) {
        this.baseHeightmapTexture = textureId;
        this.baseHeightData = heightData;
        LogInfo.println("Base heightmap set for terrain data manager");
    }

    public void applyBrushModification(float worldX, float worldZ, BrushSettings brush, 
                                     BrushType operation, float deltaTime) {
        System.out.println("🌍 TerrainDataManager.applyBrushModification called:");
        System.out.println("   📍 Position: (" + worldX + ", " + worldZ + ")");
        System.out.println("   🖌️ Operation: " + operation);
        System.out.println("   📏 Brush size: " + brush.getSize());
        System.out.println("   💪 Brush strength: " + brush.getStrength());
        
        TerrainEditAction action = new TerrainEditAction(worldX, worldZ, brush, operation);
        
        float heightDelta = calculateHeightDelta(operation, brush, deltaTime);
        
        System.out.println("⚡ Height delta calculated: " + heightDelta);
        
        switch (operation) {
            case RAISE, LOWER -> {
                System.out.println("📈 Applying height modification: " + operation);
                modificationBuffer.applyHeightModification(worldX, worldZ, heightDelta, brush, terrainScale);
            }
            case SMOOTH -> {
                System.out.println("🌊 Applying smooth operation");
                if (baseHeightData != null) {
                    modificationBuffer.smoothTerrain(worldX, worldZ, brush, terrainScale, baseHeightData);
                } else {
                    System.out.println("❌ Base height data is null - cannot smooth");
                }
            }
            case FLATTEN -> {
                System.out.println("📏 Applying flatten operation");
                float targetHeight = brush.getTargetHeight();
                float currentHeight = getCurrentHeightAtPosition(worldX, worldZ);
                float delta = (targetHeight - currentHeight) * brush.getStrength() * deltaTime;
                System.out.println("   Target: " + targetHeight + ", Current: " + currentHeight + ", Delta: " + delta);
                modificationBuffer.applyHeightModification(worldX, worldZ, delta, brush, terrainScale);
            }
            case NOISE -> {
                System.out.println("🎲 Applying noise operation");
                float noiseDelta = (float) (Math.random() - 0.5) * 2.0f * brush.getStrength() * deltaTime;
                System.out.println("   Noise delta: " + noiseDelta);
                modificationBuffer.applyHeightModification(worldX, worldZ, noiseDelta, brush, terrainScale);
            }
        }
        
        actionManager.addAction(action);
        needsTextureUpdate = true;
        System.out.println("🔄 Texture update needed - flag set to true");
        
        // CRITICAL: Actually update the texture!
        updateModificationTexture();
        System.out.println("✅ Modification texture updated!");
    }

    private float calculateHeightDelta(BrushType operation, BrushSettings brush, float deltaTime) {
        float baseDelta = brush.getStrength() * deltaTime * 50.0f;
        
        return switch (operation) {
            case RAISE -> baseDelta;
            case LOWER -> -baseDelta;
            default -> 0.0f;
        };
    }

    private float getCurrentHeightAtPosition(float worldX, float worldZ) {
        if (baseHeightData == null) return 0.0f;
        
        float u = (worldX + terrainScale * 0.5f) / terrainScale;
        float v = (worldZ + terrainScale * 0.5f) / terrainScale;
        
        int x = Math.max(0, Math.min(terrainWidth - 1, (int) (u * terrainWidth)));
        int y = Math.max(0, Math.min(terrainHeight - 1, (int) (v * terrainHeight)));
        
        int index = y * terrainWidth + x;
        if (index < baseHeightData.capacity()) {
            return baseHeightData.get(index) + modificationBuffer.getModificationData().get(index);
        }
        
        return 0.0f;
    }

    public void updateModificationTexture() {
        System.out.println("🔄 updateModificationTexture() called");
        System.out.println("   needsTextureUpdate: " + needsTextureUpdate);
        System.out.println("   hasDirtyRegions: " + modificationBuffer.hasDirtyRegions());
        
        if (!needsTextureUpdate || !modificationBuffer.hasDirtyRegions()) {
            System.out.println("❌ Skipping texture update - no changes or no dirty regions");
            return;
        }

        System.out.println("🖼️ Binding modification texture ID: " + modificationTexture);
        glBindTexture(GL_TEXTURE_2D, modificationTexture);
        
        List<TerrainModificationBuffer.ModificationRegion> updates = modificationBuffer.getPendingUpdates();
        
        if (updates.isEmpty()) {
            System.out.println("📤 Updating entire modification texture (" + terrainWidth + "x" + terrainHeight + ")");
            FloatBuffer data = modificationBuffer.getModificationData();
            System.out.println("🗂️ Modification buffer has " + data.remaining() + " floats");
            glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, terrainWidth, terrainHeight, 
                           GL_RED, GL_FLOAT, data);
        } else {
            System.out.println("📦 Updating " + updates.size() + " texture regions");
            FloatBuffer tempBuffer = BufferUtils.createFloatBuffer(terrainWidth);
            
            for (TerrainModificationBuffer.ModificationRegion region : updates) {
                int regionWidth = Math.min(region.width, terrainWidth - region.x);
                int regionHeight = Math.min(region.height, terrainHeight - region.y);
                
                if (regionWidth <= 0 || regionHeight <= 0) continue;
                
                for (int row = 0; row < regionHeight; row++) {
                    int sourceY = region.y + row;
                    if (sourceY >= terrainHeight) break;
                    
                    tempBuffer.clear();
                    
                    for (int col = 0; col < regionWidth; col++) {
                        int sourceX = region.x + col;
                        if (sourceX >= terrainWidth) break;
                        
                        int index = sourceY * terrainWidth + sourceX;
                        tempBuffer.put(modificationBuffer.getModificationData().get(index));
                    }
                    
                    tempBuffer.flip();
                    
                    glTexSubImage2D(GL_TEXTURE_2D, 0, region.x, sourceY, regionWidth, 1, 
                                   GL_RED, GL_FLOAT, tempBuffer);
                }
            }
        }
        
        glBindTexture(GL_TEXTURE_2D, 0);
        
        modificationBuffer.clearDirtyRegions();
        needsTextureUpdate = false;
    }

    public void bindTextures(int baseUnit, int modificationUnit) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + baseUnit);
        glBindTexture(GL_TEXTURE_2D, baseHeightmapTexture);
        
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + modificationUnit);
        glBindTexture(GL_TEXTURE_2D, modificationTexture);
        
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
    }

    public void undo() {
        TerrainEditAction lastAction = actionManager.undo();
        if (lastAction != null) {
            applyActionInverse(lastAction);
            needsTextureUpdate = true;
        }
    }

    public void redo() {
        TerrainEditAction redoAction = actionManager.redo();
        if (redoAction != null) {
            applyAction(redoAction);
            needsTextureUpdate = true;
        }
    }

    private void applyAction(TerrainEditAction action) {
        applyBrushModification(action.getWorldX(), action.getWorldZ(), 
                              action.getBrush(), action.getOperation(), 0.016f);
    }

    private void applyActionInverse(TerrainEditAction action) {
        BrushType inverseOperation = switch (action.getOperation()) {
            case RAISE -> BrushType.LOWER;
            case LOWER -> BrushType.RAISE;
            default -> action.getOperation();
        };
        
        applyBrushModification(action.getWorldX(), action.getWorldZ(), 
                              action.getBrush(), inverseOperation, 0.016f);
    }

    public void clearModifications() {
        modificationBuffer.clearModifications();
        actionManager.clearHistory();
        needsTextureUpdate = true;
    }

    public boolean canUndo() { return actionManager.canUndo(); }
    public boolean canRedo() { return actionManager.canRedo(); }

    public int getBaseHeightmapTexture() { return baseHeightmapTexture; }
    public int getModificationTexture() { return modificationTexture; }
    public TerrainModificationBuffer getModificationBuffer() { return modificationBuffer; }
    public int getTerrainWidth() { return terrainWidth; }
    public int getTerrainHeight() { return terrainHeight; }
    public float getTerrainScale() { return terrainScale; }

    public void cleanUp() {
        if (modificationTexture != 0) {
            glDeleteTextures(modificationTexture);
        }
        LogInfo.println("TerrainDataManager cleaned up");
    }
}