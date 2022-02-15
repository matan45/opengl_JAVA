package app.math.components;

import app.math.OLMatrix4f;
import app.math.OLVector3f;

public class OLTransform {
    private OLVector3f position;
    private OLVector3f scale;
    private OLVector3f rotation;
    private final OLMatrix4f modelMatrix;

    public OLTransform(OLVector3f position, OLVector3f scale, OLVector3f rotation) {
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
        modelMatrix = new OLMatrix4f();
    }

    public OLTransform() {
        position = new OLVector3f();
        scale = new OLVector3f(1.0f, 1.0f, 1.0f);
        rotation = new OLVector3f();
        modelMatrix = new OLMatrix4f();
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

    public OLMatrix4f getModelMatrix() {
        modelMatrix.identity();
        modelMatrix.translate(position);
        modelMatrix.rotate((float) Math.toRadians(rotation.x), OLVector3f.Xaxis);
        modelMatrix.rotate((float) Math.toRadians(rotation.y), OLVector3f.Yaxis);
        modelMatrix.rotate((float) Math.toRadians(rotation.z), OLVector3f.Zaxis);
        modelMatrix.scale(scale.absolute());
        return modelMatrix;
    }

}
