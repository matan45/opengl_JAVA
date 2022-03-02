package app.renderer.lights;

import app.math.OLVector3f;

public class DirectionalLight {
    private OLVector3f direction;
    private final OLVector3f color;
    private float dirLightIntensity;

    public DirectionalLight() {
        this.direction = new OLVector3f();
        this.color = new OLVector3f();
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
