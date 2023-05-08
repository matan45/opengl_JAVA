package app.math;

import java.io.Serializable;
import java.util.Objects;

public class OLVector3f implements Serializable {
    public float x;
    public float y;
    public float z;

    public static final OLVector3f Xaxis = new OLVector3f(1, 0, 0);
    public static final OLVector3f Yaxis = new OLVector3f(0, 1, 0);
    public static final OLVector3f Zaxis = new OLVector3f(0, 0, 1);

    public OLVector3f() {
        x = 0;
        y = 0;
        z = 0;
    }

    public OLVector3f(OLVector3f other) {
        x = other.x;
        y = other.y;
        z = other.z;
    }

    public OLVector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setOLVector3f(OLVector3f v) {
        x = v.x;
        y = v.y;
        z = v.z;
    }

    public void setOLVector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public OLVector3f sub(OLVector3f v) {
        return new OLVector3f(x - v.x, y - v.y, z - v.z);
    }

    public OLVector3f add(OLVector3f v) {
        return new OLVector3f(x + v.x, y + v.y, z + v.z);
    }

    public OLVector3f div(float d) {
        return new OLVector3f(x / d, y / d, z / d);
    }


    public OLVector3f mul(OLVector3f v) {
        return new OLVector3f(x * v.x, y * v.y, z * v.z);
    }

    public OLVector3f mul(float scalar) {
        return new OLVector3f(x * scalar, y * scalar, z * scalar);
    }

    public float lengthSquared() {
        return x * x + y * y + z * z;
    }

    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }

    public OLVector3f normalize() {
        float scalar = length();
        return new OLVector3f(x / scalar, y / scalar, z / scalar);
    }

    public OLVector3f cross(OLVector3f v) {
        float rx = y * v.z - z * v.y;
        float ry = x * v.z - z * v.x;
        float rz = x * v.y - y * v.x;
        return new OLVector3f(rx, ry, rz);
    }

    public OLVector3f negate() {
        return new OLVector3f(-x, -y, -z);
    }

    public float distance(OLVector3f v) {
        float dx = x - v.x;
        float dy = y - v.y;
        float dz = z - v.z;
        return (float) Math.sqrt(Math.fma(dx, dx, Math.fma(dy, dy, dz * dz)));
    }

    public float distanceSquared(OLVector3f v) {
        float dx = x - v.x;
        float dy = y - v.y;
        float dz = z - v.z;
        return dx * dx + dy * dy + dz * dz;
    }

    public OLVector3f min(OLVector3f v) {
        return new OLVector3f(Math.min(x, v.x), Math.min(y, v.y), Math.min(z, v.z));
    }


    public OLVector3f max(OLVector3f v) {
        return new OLVector3f(Math.max(x, v.x), Math.max(y, v.y), Math.max(z, v.z));
    }

    public OLVector3f absolute() {
        return new OLVector3f(Math.abs(x), Math.abs(y), Math.abs(z));
    }


    public float dot(OLVector3f v) {
        return x * v.x + y * v.y + z * v.z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OLVector3f vector3f = (OLVector3f) o;

        return (x == vector3f.x) && (y == vector3f.y) && (z == vector3f.z);
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
                '}';
    }
}
