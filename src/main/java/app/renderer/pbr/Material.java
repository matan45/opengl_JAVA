package app.renderer.pbr;

public class Material {
    private int albedoMap;
    private int normalMap;
    private int metallicMap;
    private int roughnessMap;
    private int aoMap;
    private int displacementMap;
    private int emissiveMap;

    public int getAlbedoMap() {
        return albedoMap;
    }

    public void setAlbedoMap(int albedoMap) {
        this.albedoMap = albedoMap;
    }

    public int getNormalMap() {
        return normalMap;
    }

    public void setNormalMap(int normalMap) {
        this.normalMap = normalMap;
    }

    public int getMetallicMap() {
        return metallicMap;
    }

    public void setMetallicMap(int metallicMap) {
        this.metallicMap = metallicMap;
    }

    public int getRoughnessMap() {
        return roughnessMap;
    }

    public void setRoughnessMap(int roughnessMap) {
        this.roughnessMap = roughnessMap;
    }

    public int getAoMap() {
        return aoMap;
    }

    public void setAoMap(int aoMap) {
        this.aoMap = aoMap;
    }

    public int getDisplacementMap() {
        return displacementMap;
    }

    public void setDisplacementMap(int displacementMap) {
        this.displacementMap = displacementMap;
    }

    public int getEmissiveMap() {
        return emissiveMap;
    }

    public void setEmissiveMap(int emissiveMap) {
        this.emissiveMap = emissiveMap;
    }
}
