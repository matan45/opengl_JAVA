package app.math.components;

import app.math.OLVector3f;

public class OLTransform {
    OLVector3f position;
    OLVector3f scale;
    OLVector3f rotation;

    public OLTransform(OLVector3f position, OLVector3f scale, OLVector3f rotation) {
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
    }

    public OLTransform() {
        this.position = new OLVector3f();
        this.scale = new OLVector3f();
        this.rotation = new OLVector3f();
    }

    public OLVector3f getPosition() {
        return position;
    }

    public void setPosition(OLVector3f position) {
        this.position = position;
    }

    public OLVector3f getScale() {
        return scale;
    }

    public void setScale(OLVector3f scale) {
        this.scale = scale;
    }

    public OLVector3f getRotation() {
        return rotation;
    }

    public void setRotation(OLVector3f rotation) {
        this.rotation = rotation;
    }
}
