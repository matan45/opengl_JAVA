package app.renderer.terrain;

import app.renderer.Textures;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;

public class TerrainMaterial implements Serializable {
    private final transient Textures textures;
    private int albedoMap;
    private int normalMap;
    private final int defaultAlbedoMap;
    private final int defaultNormalMap;
    private String albedoMapPath;
    private String normalMapPath;
    private String albedoFileName;
    private String normalFileName;

    public TerrainMaterial(Textures textures) {
        this.textures = textures;
        albedoMapPath = "";
        normalMapPath = "";

        albedoFileName = "";
        normalFileName = "";

        defaultAlbedoMap = textures.loadTexture(Path.of("resources\\material\\terrainDefaltMaterial\\albedo.jpg"));
        defaultNormalMap = textures.loadTexture(Path.of("resources\\material\\terrainDefaltMaterial\\normal.png"));

        albedoMap = defaultAlbedoMap;
        normalMap = defaultNormalMap;

    }

    public int getAlbedoMap() {
        return albedoMap;
    }

    public void albedoMapRemove() {
        albedoMapPath = "";
        albedoFileName = "";
        albedoMap = defaultAlbedoMap;
    }

    public void setAlbedoMap(String albedoMapPath) {
        if (!albedoMapPath.isEmpty()) {
            this.albedoMapPath = albedoMapPath;
            albedoFileName = new File(albedoMapPath).getName();
            albedoMap = textures.loadTexture(Path.of(albedoMapPath));
        }
    }

    public int getNormalMap() {
        return normalMap;
    }

    public void normalMapRemove() {
        normalMapPath = "";
        normalFileName = "";
        normalMap = defaultNormalMap;
    }

    public void setNormalMap(String normalMapPath) {
        if (!normalMapPath.isEmpty()) {
            this.normalMapPath = normalMapPath;
            normalFileName = new File(normalMapPath).getName();
            normalMap = textures.loadTexture(Path.of(normalMapPath));
        }
    }

    public String getAlbedoMapPath() {
        return albedoMapPath;
    }

    public String getNormalMapPath() {
        return normalMapPath;
    }
    public String getAlbedoFileName() {
        return albedoFileName;
    }

    public String getNormalFileName() {
        return normalFileName;
    }

}
