package app.renderer.ibl;

import app.math.OLMatrix4f;
import app.renderer.shaders.ShaderProgram;

import java.nio.file.Path;

public class ShaderIrradiance extends CommonShaderSkyBox {
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

    @Override
    public void connectTextureUnits() {
        super.loadInt(locationEquirectangularMap, 0);
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
