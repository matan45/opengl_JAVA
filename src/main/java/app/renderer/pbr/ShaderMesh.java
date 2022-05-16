package app.renderer.pbr;

import app.math.OLMatrix4f;
import app.math.OLVector3f;
import app.renderer.lights.DirectionalLight;
import app.renderer.lights.PointLight;
import app.renderer.lights.SpotLight;
import app.renderer.shaders.ShaderProgram;
import app.renderer.shaders.UniformsNames;

import java.nio.file.Path;
import java.util.List;


public class ShaderMesh extends ShaderProgram {
    private int locationModelMatrix;
    private int locationCameraPosition;

    private int locationAlbedoMap;
    private int locationNormalMap;
    private int locationMetallicMap;
    private int locationRoughnessMap;
    private int locationAoMap;
    private int locationEmissiveMap;

    private int locationIrradianceMap;
    private int locationPrefilterMap;
    private int locationBrdfLUT;

    private int locationDirLightDirection;
    private int locationDirLightColor;
    private int locationDirLightIntensity;

    private int locationSightRange;
    private int locationFogColor;
    private int locationIsFog;

    private static final OLVector3f defaults = new OLVector3f();

    protected ShaderMesh(Path path) {
        super(path);
    }

    @Override
    protected void getAllUniformLocations() {
        locationModelMatrix = super.getUniformLocation(UniformsNames.MODEL.getUniformsName());
        locationCameraPosition = super.getUniformLocation(UniformsNames.CAMERA_POSITION.getUniformsName());

        locationSightRange = super.getUniformLocation("sightRange");
        locationFogColor = super.getUniformLocation("fogColor");
        locationIsFog = super.getUniformLocation("isFog");

        locationDirLightDirection = super.getUniformLocation("dirLight.direction");
        locationDirLightColor = super.getUniformLocation("dirLight.color");
        locationDirLightIntensity = super.getUniformLocation("dirLightIntensity");

        locationAlbedoMap = super.getUniformLocation("albedoMap");
        locationNormalMap = super.getUniformLocation("normalMap");
        locationMetallicMap = super.getUniformLocation("metallicMap");
        locationRoughnessMap = super.getUniformLocation("roughnessMap");
        locationAoMap = super.getUniformLocation("aoMap");
        locationEmissiveMap = super.getUniformLocation("emissiveMap");

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
        super.loadInt(locationMetallicMap, 5);
        super.loadInt(locationRoughnessMap, 6);
        super.loadInt(locationAoMap, 7);
        super.loadInt(locationEmissiveMap, 8);

    }

    public void loadIsFog(boolean fog) {
        super.loadBoolean(locationIsFog, fog);
    }

    public void loadSightRange(float sightRange) {
        super.loadFloat(locationSightRange, sightRange);
    }

    public void loadFogColor(OLVector3f fogColor) {
        super.load3DVector(locationFogColor, fogColor);
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

    public void loadPointLights(List<PointLight> pointLights) {
        super.loadInt(super.getUniformLocation("pointLightSize"), pointLights.size());

        for (int i = 0; i < pointLights.size(); i++) {

            super.load3DVector(super.getUniformLocation("pointLight[" + i + "].position"),
                    pointLights.get(i).getPosition());
            super.load3DVector(super.getUniformLocation("pointLight[" + i + "].color"),
                    pointLights.get(i).getColor());
            super.loadFloat(super.getUniformLocation("pointLight[" + i + "].constant"),
                    pointLights.get(i).getConstant());
            super.loadFloat(super.getUniformLocation("pointLight[" + i + "].linear"),
                    pointLights.get(i).getLinear());
            super.loadFloat(super.getUniformLocation("pointLight[" + i + "].quadratic"),
                    pointLights.get(i).getQuadratic());
        }
    }

    public void loadSpotLights(List<SpotLight> spotLights) {
        super.loadInt(super.getUniformLocation("spotLightSize"), spotLights.size());

        for (int i = 0; i < spotLights.size(); i++) {

            super.load3DVector(super.getUniformLocation("spotLight[" + i + "].position"),
                    spotLights.get(i).getPosition());
            super.load3DVector(super.getUniformLocation("spotLight[" + i + "].direction"),
                    spotLights.get(i).getDirection());
            super.load3DVector(super.getUniformLocation("spotLight[" + i + "].color"),
                    spotLights.get(i).getColor());
            super.loadFloat(super.getUniformLocation("spotLight[" + i + "].cutOff"),
                    spotLights.get(i).getCutOff());
            super.loadFloat(super.getUniformLocation("spotLight[" + i + "].outerCutOff"),
                    spotLights.get(i).getOuterCutOff());
            super.loadFloat(super.getUniformLocation("spotLight[" + i + "].constant"),
                    spotLights.get(i).getConstant());
            super.loadFloat(super.getUniformLocation("spotLight[" + i + "].linear"),
                    spotLights.get(i).getLinear());
            super.loadFloat(super.getUniformLocation("spotLight[" + i + "].quadratic"),
                    spotLights.get(i).getQuadratic());
        }
    }
}
