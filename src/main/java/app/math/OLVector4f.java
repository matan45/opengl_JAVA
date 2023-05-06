package app.math;

import java.util.Objects;

public class OLVector4f {
    public float x;
    public float y;
    public float z;
    public float w;

    public OLVector4f() {
        x = 0;
        y = 0;
        z = 0;
    }

    public OLVector4f(OLVector4f other) {
        x = other.x;
        y = other.y;
        z = other.z;
        w = other.w;
    }

    public OLVector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OLVector4f vector4f = (OLVector4f) o;

        return (x == vector4f.x) && (y == vector4f.y) && (z == vector4f.z) && (w == vector4f.w);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this);
    }

    @Override
    public String toString() {
        return "OLVector3f{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", w=" + w +
                '}';
    }
}
