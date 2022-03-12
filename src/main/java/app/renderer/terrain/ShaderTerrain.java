package app.renderer.terrain;

import app.math.OLMatrix4f;
import app.math.OLVector3f;
import app.renderer.shaders.ShaderProgram;
import app.renderer.shaders.UniformsNames;

import java.nio.file.Path;

public class ShaderTerrain extends ShaderProgram {
    private int locationProjectionMatrix;
    private int locationViewMatrix;
    private int locationCameraPosition;
    private int locationgDispFactor;

    protected ShaderTerrain(Path path) {
        super(path);
    }

    @Override
    protected void getAllUniformLocations() {
        locationProjectionMatrix = super.getUniformLocation(UniformsNames.PROJECTION.getUniformsName());
        locationViewMatrix = super.getUniformLocation(UniformsNames.VIEW.getUniformsName());
        locationCameraPosition = super.getUniformLocation(UniformsNames.CAMERA_POSITION.getUniformsName());
        locationgDispFactor = super.getUniformLocation("gDispFactor");

    }

    public void loadViewMatrix(OLMatrix4f view) {
        super.loadMatrix(locationViewMatrix, view);
    }

    public void loadCameraPosition(OLVector3f camera) {
        super.load3DVector(locationCameraPosition, camera);
    }

    public void loadProjectionMatrix(OLMatrix4f projection) {
        super.loadMatrix(locationProjectionMatrix, projection);
    }

    public void loadgDispFactor(float dispFactor) {
        super.loadFloat(locationgDispFactor, dispFactor);
    }
}
