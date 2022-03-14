package app.renderer.terrain;

import app.math.OLMatrix4f;
import app.math.OLVector2f;
import app.renderer.shaders.ShaderProgram;
import app.renderer.shaders.UniformsNames;

import java.nio.file.Path;

public class ShaderTerrain extends ShaderProgram {
    private int locationProjectionMatrix;
    private int locationViewMatrix;
    private int locationTessellationFactor;
    private int locationModelMatrix;
    private int locationgDispFactor;
    private int locationgdisplacementMap;
    private int locationviewport;

    protected ShaderTerrain(Path path) {
        super(path);
    }

    @Override
    protected void getAllUniformLocations() {
        locationProjectionMatrix = super.getUniformLocation(UniformsNames.PROJECTION.getUniformsName());
        locationViewMatrix = super.getUniformLocation(UniformsNames.VIEW.getUniformsName());
        locationModelMatrix = super.getUniformLocation(UniformsNames.MODEL.getUniformsName());
        locationTessellationFactor = super.getUniformLocation("tessellationFactor");
        locationgDispFactor = super.getUniformLocation("gDispFactor");
        locationgdisplacementMap = super.getUniformLocation("displacementMap");
        locationviewport = super.getUniformLocation("viewPort");
    }

    public void connectTextureUnits() {
        super.loadInt(locationgdisplacementMap, 0);
    }

    public void loadViewMatrix(OLMatrix4f view) {
        super.loadMatrix(locationViewMatrix, view);
    }

    public void loadTessellationFactor(float tessellationFactor) {
        super.loadFloat(locationTessellationFactor, tessellationFactor);
    }

    public void loadProjectionMatrix(OLMatrix4f projection) {
        super.loadMatrix(locationProjectionMatrix, projection);
    }

    public void loadViewPort(OLVector2f viewPort) {
        super.load2DVector(locationviewport, viewPort);
    }

    public void loadModelMatrix(OLMatrix4f model) {
        super.loadMatrix(locationModelMatrix, model);
    }

    public void loadgDispFactor(float dispFactor) {
        super.loadFloat(locationgDispFactor, dispFactor);
    }
}
