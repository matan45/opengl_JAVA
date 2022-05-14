package app.renderer.ibl;

import app.renderer.shaders.ShaderProgram;

import java.nio.file.Path;

public final class ShaderCubeMap extends ShaderProgram {
    private int locationEnvironmentMap;
    private int locationExposure;

    ShaderCubeMap(Path path) {
        super(path);
    }

    @Override
    protected void getAllUniformLocations() {
        locationEnvironmentMap = super.getUniformLocation("environmentMap");
        locationExposure = super.getUniformLocation("exposure");
    }

    public void connectTextureUnits() {
        super.loadInt(locationEnvironmentMap, 0);
    }

    public void loadExposure(float exposure) {
        super.loadFloat(locationExposure, exposure);
    }
}
