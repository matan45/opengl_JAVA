package app.renderer.debug.billboards;


import app.math.OLMatrix4f;
import app.math.OLVector3f;
import app.renderer.shaders.ShaderProgram;
import app.renderer.shaders.UniformsNames;

import java.nio.file.Path;

public class ShaderBillboards extends ShaderProgram {
    int locationProjectionMatrix;
    int locationViewMatrix;
    int locationCenterPosition;

    protected ShaderBillboards(Path path) {
        super(path);
    }

    @Override
    protected void getAllUniformLocations() {
        locationProjectionMatrix = super.getUniformLocation(UniformsNames.PROJECTION.getUniformsName());
        locationViewMatrix = super.getUniformLocation(UniformsNames.VIEW.getUniformsName());
        locationCenterPosition = super.getUniformLocation("centerPosition");
    }

    public void loadViewMatrix(OLMatrix4f view) {
        super.loadMatrix(locationViewMatrix, view);
    }

    public void loadProjectionMatrix(OLMatrix4f projection) {
        super.loadMatrix(locationProjectionMatrix, projection);
    }

    public void loadCenterPosition(OLVector3f position) {
        super.load3DVector(locationCenterPosition, position);
    }
}
