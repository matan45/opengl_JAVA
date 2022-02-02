package app.renderer.ibl;

import app.math.OLMatrix4f;
import app.renderer.shaders.ShaderProgram;

import java.nio.file.Path;

public class ShaderIrradianceConvolution extends ShaderProgram {
    int locationProjectionMatrix;
    int locationViewMatrix;
    int locationEnvironmentMap;
    protected ShaderIrradianceConvolution(Path path) {
        super(path);
    }

    @Override
    protected void getAllUniformLocations() {
        locationProjectionMatrix = super.getUniformLocation("projection");
        locationViewMatrix = super.getUniformLocation("view");
        locationEnvironmentMap = super.getUniformLocation("environmentMap");
    }

    public void connectTextureUnits() {
        super.loadInt(locationEnvironmentMap, 0);
    }

    public void loadViewMatrix(OLMatrix4f view) {
        super.loadMatrix(locationViewMatrix, view);
    }

    public void loadProjectionMatrix(OLMatrix4f projection) {
        super.loadMatrix(locationProjectionMatrix, projection);
    }
}
