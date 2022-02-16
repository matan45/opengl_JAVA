package app.renderer.ibl;

import app.math.OLMatrix4f;
import app.renderer.shaders.UniformsNames;

import java.nio.file.Path;

public final class ShaderPreFilter extends CommonShaderSkyBox {
    private int locationEnvironmentMap;
    private int locationRoughness;

    ShaderPreFilter(Path path) {
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
        super.loadFloat(locationRoughness, roughness);
    }

    @Override
    protected void getAllUniformLocations() {
        locationProjectionMatrix = super.getUniformLocation(UniformsNames.PROJECTION.getUniformsName());
        locationViewMatrix = super.getUniformLocation(UniformsNames.VIEW.getUniformsName());
        locationEnvironmentMap = super.getUniformLocation("environmentMap");
        locationRoughness = super.getUniformLocation("roughness");
    }
}
