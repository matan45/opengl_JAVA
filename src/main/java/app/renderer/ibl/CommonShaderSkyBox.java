package app.renderer.ibl;

import app.math.OLMatrix4f;
import app.renderer.shaders.ShaderProgram;

import java.nio.file.Path;

public abstract class CommonShaderSkyBox extends ShaderProgram {
    int locationProjectionMatrix;
    int locationViewMatrix;

    protected CommonShaderSkyBox(Path path) {
        super(path);
    }

    protected abstract void connectTextureUnits();

    protected abstract void loadViewMatrix(OLMatrix4f view);

    protected abstract void loadProjectionMatrix(OLMatrix4f projection);
}
