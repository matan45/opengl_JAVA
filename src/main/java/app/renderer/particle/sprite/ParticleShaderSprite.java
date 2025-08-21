package app.renderer.particle.sprite;

import app.renderer.shaders.ShaderProgram;

import java.nio.file.Path;

public class ParticleShaderSprite extends ShaderProgram {

    protected ParticleShaderSprite(Path path) {
        super(path);
    }

    @Override
    protected void getAllUniformLocations() {
    }
}
