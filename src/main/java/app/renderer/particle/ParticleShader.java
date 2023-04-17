package app.renderer.particle;

import app.renderer.shaders.ShaderProgram;

import java.nio.file.Path;

public class ParticleShader extends ShaderProgram {
    protected ParticleShader(Path path) {
        super(path);
    }

    @Override
    protected void getAllUniformLocations() {

    }
}
