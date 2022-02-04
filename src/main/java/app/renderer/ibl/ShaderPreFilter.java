package app.renderer.ibl;

import app.math.OLMatrix4f;

import java.nio.file.Path;

public final class ShaderPreFilter extends CommonShaderSkyBox {
    int locationEnvironmentMap;
    int locationroughness;

    protected ShaderPreFilter(Path path) {
        super(path);
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

    public void loadRoughness(float roughness) {
        super.loadFloat(locationroughness, roughness);
    }

    @Override
    protected void getAllUniformLocations() {
        locationProjectionMatrix = super.getUniformLocation("projection");
        locationViewMatrix = super.getUniformLocation("view");
        locationEnvironmentMap = super.getUniformLocation("environmentMap");
        locationroughness = super.getUniformLocation("roughness");
    }
}
