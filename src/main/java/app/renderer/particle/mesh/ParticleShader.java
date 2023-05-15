package app.renderer.particle.mesh;

import app.math.OLMatrix4f;
import app.math.OLVector3f;
import app.renderer.lights.DirectionalLight;
import app.renderer.shaders.ShaderProgram;
import app.renderer.shaders.UniformsNames;

import java.nio.file.Path;

public class ParticleShader extends ShaderProgram {

    private int locationModelMatrix;
    private int locationCameraPosition;

    private int locationAlbedoMap;
    private int locationNormalMap;
    private int locationMetallic;
    private int locationRoughness;
    private int locationAo;
    private int locationEmissive;

    private int locationIrradianceMap;
    private int locationPrefilterMap;
    private int locationBrdfLUT;

    private int locationDirLightDirection;
    private int locationDirLightColor;
    private int locationDirLightIntensity;

    private static final OLVector3f defaults = new OLVector3f();
    protected ParticleShader(Path path) {
        super(path);
    }

    @Override
    protected void getAllUniformLocations() {
        locationModelMatrix = super.getUniformLocation(UniformsNames.MODEL.getUniformsName());
        locationCameraPosition = super.getUniformLocation(UniformsNames.CAMERA_POSITION.getUniformsName());

        locationDirLightDirection = super.getUniformLocation("dirLight.direction");
        locationDirLightColor = super.getUniformLocation("dirLight.color");
        locationDirLightIntensity = super.getUniformLocation("dirLightIntensity");

        locationAlbedoMap = super.getUniformLocation("albedoMap");
        locationNormalMap = super.getUniformLocation("normalMap");

        locationMetallic = super.getUniformLocation("metallicMap");
        locationRoughness = super.getUniformLocation("roughnessMap");
        locationAo = super.getUniformLocation("aoMap");
        locationEmissive = super.getUniformLocation("emissiveMap");

        locationIrradianceMap = super.getUniformLocation("irradianceMap");
        locationPrefilterMap = super.getUniformLocation("prefilterMap");
        locationBrdfLUT = super.getUniformLocation("brdfLUT");

    }

    public void connectTextureUnits() {
        super.loadInt(locationIrradianceMap, 0);
        super.loadInt(locationPrefilterMap, 1);
        super.loadInt(locationBrdfLUT, 2);

        super.loadInt(locationAlbedoMap, 3);
        super.loadInt(locationNormalMap, 4);

    }

    public void loadMetallic(float metallic) {
        super.loadFloat(locationMetallic, metallic);
    }

    public void loadRoughness(float roughness) {
        super.loadFloat(locationRoughness, roughness);
    }

    public void loadEmissive(float emissive) {
        super.loadFloat(locationEmissive, emissive);
    }

    public void loadAo(float ao) {
        super.loadFloat(locationAo, ao);
    }

    public void loadModelMatrix(OLMatrix4f model) {
        super.loadMatrix(locationModelMatrix, model);
    }

    public void loadCameraPosition(OLVector3f camera) {
        super.load3DVector(locationCameraPosition, camera);
    }

    public void loadDirLight(DirectionalLight directionalLight) {
        if (directionalLight != null) {
            super.load3DVector(locationDirLightDirection, directionalLight.getDirection());
            super.load3DVector(locationDirLightColor, directionalLight.getColor());
            super.loadFloat(locationDirLightIntensity,directionalLight.getDirLightIntensity());
        } else {
            super.load3DVector(locationDirLightDirection, defaults);
            super.load3DVector(locationDirLightColor, defaults);
        }
    }
}
