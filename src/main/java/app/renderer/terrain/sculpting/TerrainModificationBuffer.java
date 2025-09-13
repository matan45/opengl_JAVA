package app.renderer.terrain.sculpting;

import app.math.OLVector2f;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.BufferUtils;

public class TerrainModificationBuffer {
    private final int width;
    private final int height;
    private final FloatBuffer modificationData;
    private final boolean[] dirtyRegions;
    private final int regionSize = 64;
    private final int regionsX;
    private final int regionsY;
    private final List<ModificationRegion> pendingUpdates;

    public TerrainModificationBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        this.modificationData = BufferUtils.createFloatBuffer(width * height);
        
        this.regionsX = (width + regionSize - 1) / regionSize;
        this.regionsY = (height + regionSize - 1) / regionSize;
        this.dirtyRegions = new boolean[regionsX * regionsY];
        this.pendingUpdates = new ArrayList<>();
        
        clearModifications();
    }

    public void clearModifications() {
        modificationData.clear();
        for (int i = 0; i < width * height; i++) {
            modificationData.put(0.0f);
        }
        modificationData.flip();
        clearDirtyRegions();
    }

    public void applyHeightModification(float worldX, float worldZ, float heightDelta, 
                                      BrushSettings brush, float terrainScale) {
        
        OLVector2f textureCoords = worldToTextureCoords(worldX, worldZ, terrainScale);
        int centerX = (int) (textureCoords.x * width);
        int centerY = (int) (textureCoords.y * height);
        
        System.out.println("   Texture coords: (" + textureCoords.x + ", " + textureCoords.y + ")");
        System.out.println("   Center texel: (" + centerX + ", " + centerY + ")");
        
        // Convert brush size from SculptingSystem's 8192 world space to texture texels
        float actualTerrainSize = 2048.0f; // This matches SculptingSystem terrain size
        float brushRadiusTexels = (brush.getSize() / actualTerrainSize) * width;
        System.out.println("   Brush radius in texels: " + brushRadiusTexels + " (brush size: " + brush.getSize() + " world units)");
        
        int minX = Math.max(0, (int) (centerX - brushRadiusTexels));
        int maxX = Math.min(width - 1, (int) (centerX + brushRadiusTexels));
        int minY = Math.max(0, (int) (centerY - brushRadiusTexels));
        int maxY = Math.min(height - 1, (int) (centerY + brushRadiusTexels));
        
        int pixelsProcessed = 0;
        int pixelsModified = 0;
        
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                pixelsProcessed++;
                // Convert texel coordinates back to world coordinates (terrain spans 0-terrainScale)
                float texelWorldX = ((float) x / width) * terrainScale;
                float texelWorldZ = ((float) y / height) * terrainScale;
                
                if (brush.getShape().isInside(texelWorldX, texelWorldZ, worldX, worldZ, brush.getSize())) {
                    float distance = brush.getShape().getDistance(texelWorldX, texelWorldZ, worldX, worldZ);
                    float falloff = brush.calculateFalloff(distance);
                    
                    if (falloff > 0.001f) {
                        int index = y * width + x;
                        float currentMod = modificationData.get(index);
                        float newMod = currentMod + (heightDelta * falloff * brush.getStrength());
                        modificationData.put(index, newMod);
                        
                        markRegionDirty(x, y);
                        pixelsModified++;
                        
                        if (pixelsModified <= 3) { // Only log first few modifications to avoid spam
                            System.out.println("   ✅ Modified pixel at (" + x + "," + y + ") - falloff: " + falloff + ", delta: " + (heightDelta * falloff * brush.getStrength()));
                        }
                    }
                }
            }
        }
        
        System.out.println("   📊 Processing summary: " + pixelsProcessed + " pixels checked, " + pixelsModified + " pixels modified");
        
        addPendingUpdate(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    public void smoothTerrain(float worldX, float worldZ, BrushSettings brush, 
                            float terrainScale, FloatBuffer baseHeights) {
        OLVector2f textureCoords = worldToTextureCoords(worldX, worldZ, terrainScale);
        int centerX = (int) (textureCoords.x * width);
        int centerY = (int) (textureCoords.y * height);
        
        // Convert brush size from SculptingSystem's 8192 world space to texture texels
        float actualTerrainSize = 2048.0f; // This matches SculptingSystem terrain size
        float brushRadiusTexels = (brush.getSize() / actualTerrainSize) * width;
        
        int minX = Math.max(1, (int) (centerX - brushRadiusTexels));
        int maxX = Math.min(width - 2, (int) (centerX + brushRadiusTexels));
        int minY = Math.max(1, (int) (centerY - brushRadiusTexels));
        int maxY = Math.min(height - 2, (int) (centerY + brushRadiusTexels));
        
        FloatBuffer tempBuffer = BufferUtils.createFloatBuffer((maxX - minX + 1) * (maxY - minY + 1));
        
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                // Convert texel coordinates back to world coordinates (terrain spans 0-terrainScale)
                float texelWorldX = ((float) x / width) * terrainScale;
                float texelWorldZ = ((float) y / height) * terrainScale;
                
                if (brush.getShape().isInside(texelWorldX, texelWorldZ, worldX, worldZ, brush.getSize())) {
                    float distance = brush.getShape().getDistance(texelWorldX, texelWorldZ, worldX, worldZ);
                    float falloff = brush.calculateFalloff(distance);
                    
                    if (falloff > 0.001f) {
                        float avgHeight = calculateAverageHeight(x, y, baseHeights);
                        int index = y * width + x;
                        float currentHeight = baseHeights.get(index) + modificationData.get(index);
                        float smoothedHeight = lerp(currentHeight, avgHeight, falloff * brush.getStrength() * 0.1f);
                        
                        modificationData.put(index, smoothedHeight - baseHeights.get(index));
                        markRegionDirty(x, y);
                    }
                }
            }
        }
        
        addPendingUpdate(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    private float calculateAverageHeight(int x, int y, FloatBuffer baseHeights) {
        float sum = 0.0f;
        int count = 0;
        
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int nx = x + dx;
                int ny = y + dy;
                
                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int index = ny * width + nx;
                    sum += baseHeights.get(index) + modificationData.get(index);
                    count++;
                }
            }
        }
        
        return sum / count;
    }

    private float lerp(float a, float b, float t) {
        return a + t * (b - a);
    }

    private OLVector2f worldToTextureCoords(float worldX, float worldZ, float terrainScale) {
        // SculptingSystem now uses quadtree-aligned coordinates (0-2048 range after center-based conversion)
        // World coordinates from SculptingSystem are in 0-2048 range, map to 0-1 texture coords
        float actualTerrainSize = 2048.0f; // This matches SculptingSystem terrain size
        return new OLVector2f(
            worldX / actualTerrainSize,
            worldZ / actualTerrainSize
        );
    }

    private void markRegionDirty(int x, int y) {
        int regionX = x / regionSize;
        int regionY = y / regionSize;
        int regionIndex = regionY * regionsX + regionX;

        if (regionIndex >= 0 && regionIndex < dirtyRegions.length) {
            dirtyRegions[regionIndex] = true;
        }
    }

    private void addPendingUpdate(int x, int y, int width, int height) {
        pendingUpdates.add(new ModificationRegion(x, y, width, height));
    }

    public void clearDirtyRegions() {
        int clearedRegions = 0;
        for (int i = 0; i < dirtyRegions.length; i++) {
            if (dirtyRegions[i]) clearedRegions++;
            dirtyRegions[i] = false;
        }
        pendingUpdates.clear();

        System.out.println("   🧹 Cleared " + clearedRegions + " dirty regions and " + pendingUpdates.size() + " pending updates");
    }

    public List<ModificationRegion> getPendingUpdates() {
        return new ArrayList<>(pendingUpdates);
    }

    public boolean hasDirtyRegions() {
        int dirtyCount = 0;
        for (boolean dirty : dirtyRegions) {
            if (dirty) dirtyCount++;
        }
        
        return dirtyCount > 0;
    }

    public FloatBuffer getModificationData() { return modificationData; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public record ModificationRegion(int x, int y, int width, int height) {
    }
}