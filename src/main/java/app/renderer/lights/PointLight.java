package app.renderer.lights;

import app.math.OLVector3f;
import app.math.components.Camera;
import app.renderer.debug.billboards.Billboards;

import java.io.Serializable;
import java.util.Objects;

public class PointLight implements Serializable {
    private OLVector3f position;
    private final OLVector3f color;

    private float constant;
    private float linear;
    private float quadratic;
    private final transient Billboards billboards;

    public PointLight(Billboards billboards) {
        position = new OLVector3f();
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

    public OLVector3f getColor() {
        return color;
    }

    public void drawBillboards(Camera camera) {
        billboards.render(camera, position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointLight that = (PointLight) o;
        return Objects.equals(this, that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this);
    }
}
