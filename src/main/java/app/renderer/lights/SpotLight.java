package app.renderer.lights;

import app.math.OLVector3f;
import app.math.components.Camera;
import app.renderer.debug.billboards.Billboards;

import java.io.Serializable;
import java.util.Objects;

public class SpotLight implements Serializable {

    private OLVector3f position;
    private OLVector3f direction;
    private final OLVector3f color;

    private float constant;
    private float linear;
    private float quadratic;

    private float cutOff;
    private float outerCutOff;

    private final transient Billboards billboards;


    public SpotLight(Billboards billboards) {
        position = new OLVector3f();
        direction = new OLVector3f();
        color = new OLVector3f();

        this.billboards = billboards;

        constant = 0.1f;
        linear = 0.1f;
        quadratic = 0.1f;
    }

    public void setColor(float r, float g, float b) {
        color.x = r;
        color.y = g;
        color.z = b;
    }

    public void drawBillboards(Camera camera) {
        billboards.render(camera, position);
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

    public OLVector3f getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpotLight that = (SpotLight) o;
        return Objects.equals(this, that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this);
    }
}
