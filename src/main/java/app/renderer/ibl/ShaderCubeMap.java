package app.renderer.ibl;

import app.math.OLMatrix4f;

import java.nio.file.Path;

public class ShaderCubeMap extends CommonShaderSkyBox {
    int locationEnvironmentMap;

    protected ShaderCubeMap(Path path) {
        super(path);
    }

    @Override
    protected void getAllUniformLocations() {
        locationProjectionMatrix = super.getUniformLocation("projection");
        locationViewMatrix = super.getUniformLocation("view");
        locationEnvironmentMap = super.getUniformLocation("environmentMap");

    }

    @Override
    public void connectTextureUnits() {
        super.loadInt(locationEnvironmentMap, 0);
    }

    @Override
    public void loadViewMatrix(OLMatrix4f view) {
        super.loadMatrix(locationViewMatrix, view);
    }

    @Override
    public void loadProjectionMatrix(OLMatrix4f projection) {
        super.loadMatrix(locationProjectionMatrix, projection);
    }
}
