package app.renderer.pbr;

import app.renderer.Textures;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;

public class Material implements Serializable {
    private final transient Textures textures;

    private int albedoMap;
    private int normalMap;
    private int metallicMap;
    private int roughnessMap;
    private int aoMap;
    private int emissiveMap;

    private final int defaultAlbedoMap;
    private final int defaultNormalMap;
    private final int defaultMetallicMap;
    private final int defaultRoughnessMap;
    private final int defaultAoMap;

    private String albedoMapPath;
    private String normalMapPath;
    private String metallicMapPath;
    private String roughnessMapPath;
    private String aoMapPath;
    private String emissiveMapPath;

    private String albedoFileName;
    private String normalFileName;
    private String metallicFileName;
    private String roughnessFileName;
    private String aoFileName;
    private String emissiveFileName;

    public Material(Textures textures) {
        this.textures = textures;
        albedoMapPath = "";
        normalMapPath = "";
        metallicMapPath = "";
        roughnessMapPath = "";
        aoMapPath = "";
        emissiveMapPath = "";

        albedoFileName = "";
        normalFileName = "";
        metallicFileName = "";
        roughnessFileName = "";
        aoFileName = "";
        emissiveFileName = "";

        defaultAlbedoMap = textures.loadTexture(Path.of("src\\main\\resources\\material\\defaultMaterial\\albedo.png"));
        defaultNormalMap = textures.loadTexture(Path.of("src\\main\\resources\\material\\defaultMaterial\\normal.png"));
        defaultMetallicMap = textures.loadTexture(Path.of("src\\main\\resources\\material\\defaultMaterial\\metallic.png"));
        defaultRoughnessMap = textures.loadTexture(Path.of("src\\main\\resources\\material\\defaultMaterial\\roughness.png"));
        defaultAoMap = textures.loadTexture(Path.of("src\\main\\resources\\material\\defaultMaterial\\ao.png"));

        albedoMap = defaultAlbedoMap;
        normalMap = defaultNormalMap;
        metallicMap = defaultMetallicMap;
        roughnessMap = defaultRoughnessMap;
        aoMap = defaultAoMap;
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

    public int getMetallicMap() {
        return metallicMap;
    }

    public void metallicMapRemove() {
        metallicMapPath = "";
        metallicFileName = "";
        metallicMap = defaultMetallicMap;
    }

    public void setMetallicMap(String metallicMapPath) {
        if (!metallicMapPath.isEmpty()) {
            this.metallicMapPath = metallicMapPath;
            metallicFileName = new File(metallicMapPath).getName();
            metallicMap = textures.loadTexture(Path.of(metallicMapPath));
        }
    }

    public int getRoughnessMap() {
        return roughnessMap;
    }

    public void roughnessMapRemove() {
        roughnessMapPath = "";
        roughnessFileName = "";
        roughnessMap = defaultRoughnessMap;
    }

    public void setRoughnessMap(String roughnessMapPath) {
        if (!roughnessMapPath.isEmpty()) {
            this.roughnessMapPath = roughnessMapPath;
            roughnessFileName = new File(roughnessMapPath).getName();
            roughnessMap = textures.loadTexture(Path.of(roughnessMapPath));
        }
    }

    public int getAoMap() {
        return aoMap;
    }

    public void aoMapRemove() {
        aoMapPath = "";
        aoFileName = "";
        aoMap = defaultAoMap;
    }

    public void setAoMap(String aoMapPath) {
        if (!aoMapPath.isEmpty()) {
            this.aoMapPath = aoMapPath;
            aoFileName = new File(aoMapPath).getName();
            aoMap = textures.loadTexture(Path.of(aoMapPath));
        }
    }

    public int getEmissiveMap() {
        return emissiveMap;
    }

    public void emissiveMapRemove() {
        emissiveMapPath = "";
        emissiveFileName = "";
        emissiveMap = 0;
    }

    public void setEmissiveMap(String emissiveMapPath) {
        if (!emissiveMapPath.isEmpty()) {
            this.emissiveMapPath = emissiveMapPath;
            emissiveFileName = new File(emissiveMapPath).getName();
            emissiveMap = textures.loadTexture(Path.of(emissiveMapPath));
        }
    }

    public String getAlbedoMapPath() {
        return albedoMapPath;
    }

    public String getNormalMapPath() {
        return normalMapPath;
    }

    public String getMetallicMapPath() {
        return metallicMapPath;
    }

    public String getRoughnessMapPath() {
        return roughnessMapPath;
    }

    public String getAoMapPath() {
        return aoMapPath;
    }

    public String getEmissiveMapPath() {
        return emissiveMapPath;
    }

    public String getAlbedoFileName() {
        return albedoFileName;
    }

    public String getNormalFileName() {
        return normalFileName;
    }

    public String getMetallicFileName() {
        return metallicFileName;
    }

    public String getRoughnessFileName() {
        return roughnessFileName;
    }

    public String getAoFileName() {
        return aoFileName;
    }

    public String getEmissiveFileName() {
        return emissiveFileName;
    }
}
