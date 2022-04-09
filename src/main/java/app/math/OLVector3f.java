package app.math;

import java.io.Serializable;

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

    public OLVector3f setOLVector3f(OLVector3f v) {
        x = v.x;
        y = v.y;
        z = v.z;
        return this;
    }

    public OLVector3f setOLVector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public OLVector3f sub(OLVector3f v) {
        x -= v.x;
        y -= v.y;
        z -= v.z;
        return this;
    }

    public OLVector3f add(OLVector3f v) {
        x += v.x;
        y += v.y;
        z += v.z;
        return this;
    }


    public OLVector3f mul(OLVector3f v) {
        x *= v.x;
        y *= v.y;
        z *= v.z;
        return this;
    }

    public OLVector3f mul(float scalar) {
        x += scalar;
        y += scalar;
        z += scalar;
        return this;
    }

    public float lengthSquared() {
        return x * x + y * y + z * z;
    }

    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }

    public OLVector3f normalize() {
        float scalar = length();
        x /= scalar;
        y /= scalar;
        z /= scalar;
        return this;
    }

    public OLVector3f cross(OLVector3f v) {
        float rx = y * v.z - z * v.y;
        float ry = x * v.z - z * v.x;
        float rz = x * v.y - y * v.x;
        x = rx;
        y = ry;
        z = rz;
        return this;
    }

    public OLVector3f negate() {
        x = -x;
        y = -y;
        z = -z;
        return this;
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
        x = Math.min(x, v.x);
        y = Math.min(y, v.y);
        z = Math.min(z, v.z);
        return this;
    }


    public OLVector3f max(OLVector3f v) {
        x = Math.max(x, v.x);
        y = Math.max(y, v.y);
        z = Math.max(z, v.z);
        return this;
    }

    public OLVector3f absolute() {
        x = Math.abs(x);
        y = Math.abs(y);
        z = Math.abs(z);
        return this;
    }

    public float dot(OLVector3f v) {
        return x * v.x + y * v.y + z * v.z;
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
