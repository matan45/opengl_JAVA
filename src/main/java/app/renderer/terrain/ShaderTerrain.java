package app.renderer.terrain;

import app.renderer.shaders.ShaderProgram;

import java.nio.file.Path;

public class ShaderTerrain extends ShaderProgram {
    protected ShaderTerrain(Path path) {
        super(path);
    }

    @Override
    protected void getAllUniformLocations() {

    }
}
