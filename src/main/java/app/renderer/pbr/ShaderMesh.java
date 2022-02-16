package app.renderer.pbr;

import app.math.OLMatrix4f;
import app.math.OLVector3f;
import app.renderer.shaders.ShaderProgram;
import app.renderer.shaders.UniformsNames;

import java.nio.file.Path;


public class ShaderMesh extends ShaderProgram {
    private int locationProjectionMatrix;
    private int locationViewMatrix;
    private int locationModelMatrix;
    private int locationCameraPosition;

    private int locationAlbedoMap;
    private int locationNormalMap;
    private int locationMetallicMap;
    private int locationRoughnessMap;
    private int locationAoMap;
    private int locationDisplacementMap;
    private int locationEmissiveMap;

    private int locationIrradianceMap;
    private int locationPrefilterMap;
    private int locationBrdfLUT;

    ShaderMesh(Path path) {
        super(path);
    }

    @Override
    protected void getAllUniformLocations() {
        locationProjectionMatrix = super.getUniformLocation(UniformsNames.PROJECTION.getUniformsName());
        locationViewMatrix = super.getUniformLocation(UniformsNames.VIEW.getUniformsName());
        locationModelMatrix = super.getUniformLocation(UniformsNames.MODEL.getUniformsName());
        locationCameraPosition = super.getUniformLocation(UniformsNames.CAMERA_POSITION.getUniformsName());

        locationAlbedoMap = super.getUniformLocation("albedoMap");
        locationNormalMap = super.getUniformLocation("normalMap");
        locationMetallicMap = super.getUniformLocation("metallicMap");
        locationRoughnessMap = super.getUniformLocation("roughnessMap");
        locationAoMap = super.getUniformLocation("aoMap");
        locationDisplacementMap = super.getUniformLocation("displacementMap");
        locationEmissiveMap = super.getUniformLocation("emissiveMap");

        locationIrradianceMap = super.getUniformLocation("irradianceMap");
        locationPrefilterMap = super.getUniformLocation("prefilterMap");
        locationBrdfLUT = super.getUniformLocation("brdfLUT");

    }

    public void connectTextureUnits() {
        super.loadInt(locationAlbedoMap, 0);
        super.loadInt(locationNormalMap, 1);
        super.loadInt(locationMetallicMap, 2);
        super.loadInt(locationRoughnessMap, 3);
        super.loadInt(locationAoMap, 4);
        super.loadInt(locationDisplacementMap, 5);
        super.loadInt(locationEmissiveMap, 6);

        super.loadInt(locationIrradianceMap, 7);
        super.loadInt(locationPrefilterMap, 8);
        super.loadInt(locationBrdfLUT, 9);
    }

    public void loadViewMatrix(OLMatrix4f view) {
        super.loadMatrix(locationViewMatrix, view);
    }

    public void loadModelMatrix(OLMatrix4f model) {
        super.loadMatrix(locationModelMatrix, model);
    }

    public void loadProjectionMatrix(OLMatrix4f projection) {
        super.loadMatrix(locationProjectionMatrix, projection);
    }

    public void loadCameraPosition(OLVector3f camera) {
        super.load3DVector(locationCameraPosition, camera);
    }
}
