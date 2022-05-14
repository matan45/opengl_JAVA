package app.renderer.debug.grid;

import app.renderer.shaders.ShaderProgram;

import java.nio.file.Path;

public class ShaderGrid extends ShaderProgram {

    private int locationFar;
    private int locationNear;

    protected ShaderGrid(Path path) {
        super(path);
    }

    @Override
    protected void getAllUniformLocations() {
        locationFar = super.getUniformLocation("far");
        locationNear = super.getUniformLocation("near");
    }


    public void loadFar(float far) {
        super.loadFloat(locationFar, far);
    }

    public void loadNear(float near) {
        super.loadFloat(locationNear, near);
    }

}
