package app.renderer.lights;

import app.math.OLVector3f;

public class DirectionalLight {
    private OLVector3f direction;
    private OLVector3f color;

    public DirectionalLight(OLVector3f direction, OLVector3f color) {
        this.direction = direction;
        this.color = color;
    }

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

    public void setColor(OLVector3f color) {
        this.color = color;
    }
}
