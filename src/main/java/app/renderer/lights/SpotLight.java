package app.renderer.lights;

import app.math.OLVector3f;

public class SpotLight {

    private OLVector3f position;
    private OLVector3f direction;
    private final OLVector3f color;

    private float constant;
    private float linear;
    private float quadratic;

    private float cutOff;
    private float outerCutOff;


    public SpotLight() {
        position = new OLVector3f();
        direction = new OLVector3f();
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

    public OLVector3f getDirection() {
        return direction;
    }

    public void setDirection(OLVector3f direction) {
        this.direction = direction;
    }

    public float getCutOff() {
        return cutOff;
    }

    public void setCutOff(float cutOff) {
        this.cutOff = cutOff;
    }

    public float getOuterCutOff() {
        return outerCutOff;
    }

    public void setOuterCutOff(float outerCutOff) {
        this.outerCutOff = outerCutOff;
    }
}
