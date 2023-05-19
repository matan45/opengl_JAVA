package app.renderer.particle.sprite;

import app.math.OLMatrix4f;
import app.renderer.shaders.ShaderProgram;
import app.renderer.shaders.UniformsNames;

import java.nio.file.Path;

public class ParticleShaderSprite extends ShaderProgram {

    protected ParticleShaderSprite(Path path) {
        super(path);
    }

    @Override
    protected void getAllUniformLocations() {
    }
}
