package app.renderer.terrain.sculpting;

import app.utilities.logger.LogInfo;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;

public class TerrainPaintManager {
    private final int terrainWidth;
    private final int terrainHeight;
    private final float terrainScale;

    private int splatMapTexture;
    private final TerrainSplatBuffer splatBuffer;
    
    private boolean needsTextureUpdate = false;

    public TerrainPaintManager(int width, int height, float scale) {
        this.terrainWidth = width;
        this.terrainHeight = height;
        this.terrainScale = scale;

        this.splatBuffer = new TerrainSplatBuffer(width, height);
        initializeTexture();

        LogInfo.println("TerrainPaintManager initialized: " + width + "x" + height);
    }

    private void initializeTexture() {
        splatMapTexture = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, splatMapTexture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        // Upload initial data (all Red channel)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, terrainWidth, terrainHeight, 0, GL_RGBA, GL_FLOAT, splatBuffer.getSplatData());
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void applyPaint(float worldX, float worldZ, int channelIndex, BrushSettings brush, float deltaTime) {
        if (channelIndex < 0 || channelIndex > 3) return;
        
        // System.out.println("Painting Channel: " + channelIndex + ", Delta: " + deltaTime);

        float strength = deltaTime * 5.0f; // Speed multiplier
        splatBuffer.applyPaint(worldX, worldZ, channelIndex, strength, brush, terrainScale);
        
        needsTextureUpdate = true;
    }

    public void updateSplatTexture() {
        if (!needsTextureUpdate || !splatBuffer.hasDirtyRegions()) {
            return;
        }

        glBindTexture(GL_TEXTURE_2D, splatMapTexture);

        List<TerrainSplatBuffer.ModificationRegion> updates = splatBuffer.getPendingUpdates();

        if (updates.isEmpty()) {
            // Full update fallback
            glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, terrainWidth, terrainHeight,
                    GL_RGBA, GL_FLOAT, splatBuffer.getSplatData());
        } else {
            // RGBA = 4 floats
            FloatBuffer tempBuffer = BufferUtils.createFloatBuffer(terrainWidth * 4); 

            for (TerrainSplatBuffer.ModificationRegion region : updates) {
                int regionWidth = Math.min(region.width(), terrainWidth - region.x());
                int regionHeight = Math.min(region.height(), terrainHeight - region.y());

                if (regionWidth <= 0 || regionHeight <= 0) continue;

                for (int row = 0; row < regionHeight; row++) {
                    int sourceY = region.y() + row;
                    if (sourceY >= terrainHeight) break;

                    tempBuffer.clear();
                    // tempBuffer capacity needs to be at least regionWidth * 4
                    // But we reused a buffer of size terrainWidth * 4, which is safe since regionWidth <= terrainWidth
                    
                    // Copy row data
                    int startPixelIndex = (sourceY * terrainWidth + region.x()) * 4;
                    int lengthFloats = regionWidth * 4;
                    
                    // Manual copy to temp buffer for the sub-image row
                    for(int i=0; i<lengthFloats; i++) {
                        tempBuffer.put(splatBuffer.getSplatData().get(startPixelIndex + i));
                    }
                    
                    tempBuffer.flip();

                    glTexSubImage2D(GL_TEXTURE_2D, 0, region.x(), sourceY, regionWidth, 1,
                            GL_RGBA, GL_FLOAT, tempBuffer);
                }
            }
        }

        glBindTexture(GL_TEXTURE_2D, 0);
        splatBuffer.clearDirtyRegions();
        needsTextureUpdate = false;
    }

    public int getSplatMapTexture() {
        return splatMapTexture;
    }
    
    public void cleanUp() {
        if (splatMapTexture != 0) {
            glDeleteTextures(splatMapTexture);
        }
    }
}
