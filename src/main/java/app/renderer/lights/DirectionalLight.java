package app.renderer.lights;

import app.math.OLVector3f;
import app.math.components.Camera;
import app.renderer.debug.billboards.Billboards;

public class DirectionalLight {
    private OLVector3f direction;
    private final OLVector3f position;
    private final OLVector3f color;
    private float dirLightIntensity;
    private final Billboards billboards;

    public DirectionalLight(Billboards billboards) {
        this.direction = new OLVector3f();
        this.color = new OLVector3f(254f, 254f, 254f);
        this.billboards = billboards;
        position = new OLVector3f(3, 3, 3);
    }

    public void drawBillboards(Camera camera) {
        billboards.render(camera, position);
    }

    public OLVector3f getDirection() {
        return direction;
    }

    public void setDirection(OLVector3f direction) {
        this.direction = direction;
    }

    public OLVector3f getColor() {
        return color;
    }

    public float getDirLightIntensity() {
        return dirLightIntensity;
    }

    public void setDirLightIntensity(float dirLightIntensity) {
        this.dirLightIntensity = dirLightIntensity;
    }

    public void setColor(float r, float g, float b) {
        color.x = r;
        color.y = g;
        color.z = b;
    }
}
