package app.renderer.debug.grid;

import app.math.OLMatrix4f;
import app.renderer.shaders.ShaderProgram;
import app.renderer.shaders.UniformsNames;

import java.nio.file.Path;

public class ShaderGrid extends ShaderProgram {
    private int locationProjectionMatrix;
    private int locationViewMatrix;
    private int locationFar;
    private int locationNear;

    protected ShaderGrid(Path path) {
        super(path);
    }

    @Override
    protected void getAllUniformLocations() {
        locationProjectionMatrix = super.getUniformLocation(UniformsNames.PROJECTION.getUniformsName());
        locationViewMatrix = super.getUniformLocation(UniformsNames.VIEW.getUniformsName());
        locationFar = super.getUniformLocation("far");
        locationNear = super.getUniformLocation("near");
    }


    public void loadViewMatrix(OLMatrix4f view) {
        super.loadMatrix(locationViewMatrix, view);
    }

    public void loadFar(float far) {
        super.loadFloat(locationFar, far);
    }

    public void loadNear(float near) {
        super.loadFloat(locationNear, near);
    }

    public void loadProjectionMatrix(OLMatrix4f projection) {
        super.loadMatrix(locationProjectionMatrix, projection);
    }
}
