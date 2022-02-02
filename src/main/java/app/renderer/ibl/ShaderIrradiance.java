package app.renderer.ibl;

import app.math.OLMatrix4f;
import app.renderer.shaders.ShaderProgram;

import java.nio.file.Path;

public class ShaderIrradiance extends ShaderProgram {
    int locationProjectionMatrix;
    int locationViewMatrix;
    int locationEquirectangularMap;

    protected ShaderIrradiance(Path path) {
        super(path);
    }

    @Override
    protected void getAllUniformLocations() {
        locationProjectionMatrix = super.getUniformLocation("projection");
        locationViewMatrix = super.getUniformLocation("view");
        locationEquirectangularMap = super.getUniformLocation("equirectangularMap");
    }

    public void connectTextureUnits() {
        super.loadInt(locationEquirectangularMap, 0);
    }

    public void loadViewMatrix(OLMatrix4f view) {
        super.loadMatrix(locationViewMatrix, view);
    }

    public void loadProjectionMatrix(OLMatrix4f projection) {
        super.loadMatrix(locationProjectionMatrix, projection);
    }
}
