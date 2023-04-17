package app.renderer.particle;

import app.renderer.Textures;

import java.io.File;
import java.nio.file.Path;

public class ParticleMaterial {
    private final transient Textures textures;

    private int albedoMap;
    private int normalMap;

    private final int defaultAlbedoMap;
    private final int defaultNormalMap;

    private String albedoMapPath;
    private String normalMapPath;

    private String albedoFileName;
    private String normalFileName;

    private float metallic;
    private float roughness;
    private float ao;
    private float emissive;

    public ParticleMaterial(Textures textures) {
        this.textures = textures;
        albedoMapPath = "";
        normalMapPath = "";

        albedoFileName = "";
        normalFileName = "";

        defaultAlbedoMap = textures.loadTexture(Path.of("src\\main\\resources\\material\\defaultMaterial\\albedo.png"));
        defaultNormalMap = textures.loadTexture(Path.of("src\\main\\resources\\material\\defaultMaterial\\normal.png"));

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

    public float getMetallic() {
        return metallic;
    }

    public void setMetallic(float metallic) {
        this.metallic = metallic;
    }

    public float getRoughness() {
        return roughness;
    }

    public void setRoughness(float roughness) {
        this.roughness = roughness;
    }

    public float getAo() {
        return ao;
    }

    public void setAo(float ao) {
        this.ao = ao;
    }

    public float getEmissive() {
        return emissive;
    }

    public void setEmissive(float emissive) {
        this.emissive = emissive;
    }

    public String getAlbedoFileName() {
        return albedoFileName;
    }

    public String getNormalFileName() {
        return normalFileName;
    }

    public String getAlbedoMapPath() {
        return albedoMapPath;
    }

    public String getNormalMapPath() {
        return normalMapPath;
    }

}
