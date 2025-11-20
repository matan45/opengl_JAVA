package app.renderer.terrain;

import app.renderer.Textures;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;

public class TerrainMaterial implements Serializable {
    private final transient Textures textures;
    
    // Arrays for 4 material layers
    private final int[] albedoMaps = new int[4];
    private final int[] normalMaps = new int[4];
    private final String[] albedoMapPaths = new String[4];
    private final String[] normalMapPaths = new String[4];
    private final String[] albedoFileNames = new String[4];
    private final String[] normalFileNames = new String[4];

    private final int defaultAlbedoMap;
    private final int defaultNormalMap;

    public TerrainMaterial(Textures textures) {
        this.textures = textures;
        
        defaultAlbedoMap = textures.loadTexture(Path.of("src\\main\\resources\\material\\terrainDefaltMaterial\\albedo.jpg"));
        defaultNormalMap = textures.loadTexture(Path.of("src\\main\\resources\\material\\terrainDefaltMaterial\\normal.png"));

        // Initialize all slots with default
        for (int i = 0; i < 4; i++) {
            albedoMaps[i] = defaultAlbedoMap;
            normalMaps[i] = defaultNormalMap;
            albedoMapPaths[i] = "";
            normalMapPaths[i] = "";
            albedoFileNames[i] = "";
            normalFileNames[i] = "";
        }
    }

    // --- Getters by Index ---

    public int getAlbedoMap(int index) {
        if (index >= 0 && index < 4) return albedoMaps[index];
        return defaultAlbedoMap;
    }

    public int getNormalMap(int index) {
        if (index >= 0 && index < 4) return normalMaps[index];
        return defaultNormalMap;
    }
    
    public String getAlbedoFileName(int index) {
        if (index >= 0 && index < 4) return albedoFileNames[index];
        return "";
    }

    public String getNormalFileName(int index) {
        if (index >= 0 && index < 4) return normalFileNames[index];
        return "";
    }
    
    public String getAlbedoMapPath(int index) {
        if (index >= 0 && index < 4) return albedoMapPaths[index];
        return "";
    }

    public String getNormalMapPath(int index) {
        if (index >= 0 && index < 4) return normalMapPaths[index];
        return "";
    }

    // --- Setters by Index ---

    public void setAlbedoMap(int index, String path) {
        if (index < 0 || index >= 4) return;
        
        if (!path.isEmpty()) {
            this.albedoMapPaths[index] = path;
            this.albedoFileNames[index] = new File(path).getName();
            this.albedoMaps[index] = textures.loadTexture(Path.of(path));
        }
    }
    
    public void setNormalMap(int index, String path) {
        if (index < 0 || index >= 4) return;
        
        if (!path.isEmpty()) {
            this.normalMapPaths[index] = path;
            this.normalFileNames[index] = new File(path).getName();
            this.normalMaps[index] = textures.loadTexture(Path.of(path));
        }
    }

    public void removeAlbedoMap(int index) {
        if (index < 0 || index >= 4) return;
        albedoMapPaths[index] = "";
        albedoFileNames[index] = "";
        albedoMaps[index] = defaultAlbedoMap;
    }

    public void removeNormalMap(int index) {
        if (index < 0 || index >= 4) return;
        normalMapPaths[index] = "";
        normalFileNames[index] = "";
        normalMaps[index] = defaultNormalMap;
    }

    // --- Legacy/Convenience Methods (Targeting Index 0) ---

    public int getAlbedoMap() { return albedoMaps[0]; }
    public int getNormalMap() { return normalMaps[0]; }
    public String getAlbedoFileName() { return albedoFileNames[0]; }
    public String getNormalFileName() { return normalFileNames[0]; }
    public void setAlbedoMap(String path) { setAlbedoMap(0, path); }
    public void setNormalMap(String path) { setNormalMap(0, path); }
    public void albedoMapRemove() { removeAlbedoMap(0); }
    public void normalMapRemove() { removeNormalMap(0); }
    
    public String getAlbedoMapPath() { return albedoMapPaths[0]; }
    public String getNormalMapPath() { return normalMapPaths[0]; }
}
