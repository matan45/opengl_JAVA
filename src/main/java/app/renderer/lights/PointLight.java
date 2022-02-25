package app.renderer.lights;

import app.math.OLVector3f;

public class PointLight {
    private OLVector3f position;
    private final OLVector3f color;

    private float constant;
    private float linear;
    private float quadratic;

    public PointLight() {
        position = new OLVector3f();
        color = new OLVector3f();
    }

    public void setColor(float r, float g, float b) {
        color.x = r;
        color.y = g;
        color.z = b;
    }

    public OLVector3f getPosition() {
        return position;
    }

    public void setPosition(OLVector3f position) {
        this.position = position;
    }

    public float getConstant() {
        return constant;
    }

    public void setConstant(float constant) {
        this.constant = constant;
    }

    public float getLinear() {
        return linear;
    }

    public void setLinear(float linear) {
        this.linear = linear;
    }

    public float getQuadratic() {
        return quadratic;
    }

    public void setQuadratic(float quadratic) {
        this.quadratic = quadratic;
    }
}
