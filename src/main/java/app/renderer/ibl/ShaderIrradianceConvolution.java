package app.renderer.ibl;

import app.math.OLMatrix4f;
import app.renderer.shaders.UniformsNames;

import java.nio.file.Path;

public final class ShaderIrradianceConvolution extends CommonShaderSkyBox {
    private int locationEnvironmentMap;

    ShaderIrradianceConvolution(Path path) {
        super(path);
    }

    @Override
    protected void getAllUniformLocations() {
        locationProjectionMatrix = super.getUniformLocation(UniformsNames.PROJECTION.getUniformsName());
        locationViewMatrix = super.getUniformLocation(UniformsNames.VIEW.getUniformsName());
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
