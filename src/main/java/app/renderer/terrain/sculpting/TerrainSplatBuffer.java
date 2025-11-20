package app.renderer.terrain.sculpting;

import app.math.OLVector2f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class TerrainSplatBuffer {
    private final int width;
    private final int height;
    private final FloatBuffer splatData; // RGBA interleaved: R, G, B, A, R, G...
    private final boolean[] dirtyRegions;
    private final int regionSize = 64;
    private final int regionsX;
    private final int regionsY;
    private final List<ModificationRegion> pendingUpdates;

    public TerrainSplatBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        // 4 float channels per pixel
        this.splatData = BufferUtils.createFloatBuffer(width * height * 4);

        this.regionsX = (width + regionSize - 1) / regionSize;
        this.regionsY = (height + regionSize - 1) / regionSize;
        this.dirtyRegions = new boolean[regionsX * regionsY];
        this.pendingUpdates = new ArrayList<>();

        clearSplatMap();
    }

    public void clearSplatMap() {
        splatData.clear();
        for (int i = 0; i < width * height; i++) {
            // Default to Red channel (First material) = 1.0, others 0.0
            splatData.put(1.0f); // R
            splatData.put(0.0f); // G
            splatData.put(0.0f); // B
            splatData.put(0.0f); // A
        }
        splatData.flip();
        clearDirtyRegions();
    }

    public void applyPaint(float worldX, float worldZ, int channelIndex, float strength,
                           BrushSettings brush, float terrainScale) {

        OLVector2f textureCoords = worldToTextureCoords(worldX, worldZ, terrainScale);
        int centerX = (int) (textureCoords.x * width);
        int centerY = (int) (textureCoords.y * height);

        // Convert brush size to texels
        float actualTerrainSize = 2048.0f;
        float brushRadiusTexels = (brush.getSize() / actualTerrainSize) * width;

        int minX = Math.max(0, (int) (centerX - brushRadiusTexels));
        int maxX = Math.min(width - 1, (int) (centerX + brushRadiusTexels));
        int minY = Math.max(0, (int) (centerY - brushRadiusTexels));
        int maxY = Math.min(height - 1, (int) (centerY + brushRadiusTexels));

        boolean anyModified = false;

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                float texelWorldX = ((float) x / width) * terrainScale;
                float texelWorldZ = ((float) y / height) * terrainScale;

                if (brush.getShape().isInside(texelWorldX, texelWorldZ, worldX, worldZ, brush.getSize())) {
                    float distance = brush.getShape().getDistance(texelWorldX, texelWorldZ, worldX, worldZ);
                    float falloff = brush.calculateFalloff(distance);

                    if (falloff > 0.001f) {
                        // Calculate pixel index (stride of 4)
                        int pixelIndex = (y * width + x) * 4;

                        float addedWeight = strength * falloff * brush.getStrength() * 2.0f;
                        
                        modifyPixelWeight(pixelIndex, channelIndex, addedWeight);

                        markRegionDirty(x, y);
                        anyModified = true;
                    }
                }
            }
        }

        if (anyModified) {
            addPendingUpdate(minX, minY, maxX - minX + 1, maxY - minY + 1);
        }
    }

    private void modifyPixelWeight(int pixelIndex, int targetChannel, float addedWeight) {
        // Get current weights
        float[] weights = new float[4];
        weights[0] = splatData.get(pixelIndex);
        weights[1] = splatData.get(pixelIndex + 1);
        weights[2] = splatData.get(pixelIndex + 2);
        weights[3] = splatData.get(pixelIndex + 3);

        // Increase target channel
        weights[targetChannel] = Math.min(1.0f, weights[targetChannel] + addedWeight);

        // Normalize others to maintain sum = 1.0
        float sumOthers = 0.0f;
        for (int i = 0; i < 4; i++) {
            if (i != targetChannel) sumOthers += weights[i];
        }

        float targetSumOthers = 1.0f - weights[targetChannel];

        if (sumOthers > 0.0001f) {
            float scale = targetSumOthers / sumOthers;
            for (int i = 0; i < 4; i++) {
                if (i != targetChannel) {
                    weights[i] *= scale;
                }
            }
        } else {
            // If others were 0, but we reduced target below 1 (not possible with addition logic, but for safety)
            // For addition logic, we clamp target to 1, so others become 0.
             for (int i = 0; i < 4; i++) {
                if (i != targetChannel) weights[i] = 0.0f;
            }
        }

        // Write back
        splatData.put(pixelIndex, weights[0]);
        splatData.put(pixelIndex + 1, weights[1]);
        splatData.put(pixelIndex + 2, weights[2]);
        splatData.put(pixelIndex + 3, weights[3]);
    }

    private OLVector2f worldToTextureCoords(float worldX, float worldZ, float terrainScale) {
        float actualTerrainSize = 2048.0f;
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
        for (int i = 0; i < dirtyRegions.length; i++) {
            dirtyRegions[i] = false;
        }
        pendingUpdates.clear();
    }

    public boolean hasDirtyRegions() {
        for (boolean dirty : dirtyRegions) {
            if (dirty) return true;
        }
        return false;
    }

    public List<ModificationRegion> getPendingUpdates() {
        return new ArrayList<>(pendingUpdates);
    }

    public FloatBuffer getSplatData() { return splatData; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public record ModificationRegion(int x, int y, int width, int height) {
    }
}
