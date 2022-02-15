package app.renderer.ibl;

import app.renderer.shaders.ShaderProgram;

import java.nio.file.Path;

public class Shaderbrdf extends ShaderProgram {
    Shaderbrdf(Path path) {
        super(path);
    }

    @Override
    protected void getAllUniformLocations() {

    }
}
