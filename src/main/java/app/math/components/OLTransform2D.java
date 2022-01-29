package app.math.components;

import app.math.OLMatrix4f;
import app.math.OLVector2f;
import app.math.OLVector3f;

public class OLTransform2D {
    OLVector2f position;
    OLVector2f scale;
    OLVector2f rotation;
    OLMatrix4f modelMatrix;

    public OLTransform2D(OLVector2f position, OLVector2f scale, OLVector2f rotation) {
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
    }

    public OLTransform2D() {
        this.position = new OLVector2f();
        this.scale = new OLVector2f();
        this.rotation = new OLVector2f();
    }

    public OLVector2f getPosition() {
        return position;
    }

    public void setPosition(OLVector2f position) {
        this.position = position;
    }

    public OLVector2f getScale() {
        return scale;
    }

    public void setScale(OLVector2f scale) {
        this.scale = scale;
    }

    public OLVector2f getRotation() {
        return rotation;
    }

    public void setRotation(OLVector2f rotation) {
        this.rotation = rotation;
    }

    public OLMatrix4f getModelMatrix() {
        modelMatrix.identity();
        modelMatrix.translate(position);
        modelMatrix.rotate((float) Math.toRadians(rotation.x), OLVector3f.Xaxis);
        modelMatrix.rotate((float) Math.toRadians(rotation.y), OLVector3f.Yaxis);
        modelMatrix.scale(scale.absolute());
        return modelMatrix;
    }
}
