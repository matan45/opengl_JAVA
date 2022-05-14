package app.renderer.debug.billboards;


import app.math.OLVector3f;
import app.renderer.shaders.ShaderProgram;

import java.nio.file.Path;

public class ShaderBillboards extends ShaderProgram {
    int locationCenterPosition;
    int locationImageIcon;

    protected ShaderBillboards(Path path) {
        super(path);
    }

    @Override
    protected void getAllUniformLocations() {
        locationCenterPosition = super.getUniformLocation("centerPosition");
        locationImageIcon = super.getUniformLocation("imageIcon");
    }

    public void connectTextureUnits() {
        super.loadInt(locationImageIcon, 2);
    }

    public void loadCenterPosition(OLVector3f position) {
        super.load3DVector(locationCenterPosition, position);
    }
}
