package app.renderer.particle;

import app.math.components.Camera;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;
import app.renderer.ibl.SkyBox;
import app.renderer.lights.LightHandler;

import java.util.Objects;

public class ParticleRenderer {
    public ParticleRenderer(Camera editorCamera, OpenGLObjects openGLObjects, Textures textures, SkyBox skyBox, LightHandler lightHandler) {
    }

    public void renderer() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticleRenderer that = (ParticleRenderer) o;
        return Objects.equals(this, that);
    }
}
