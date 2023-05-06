package app.math;

import java.io.Serializable;

public class OLVector2f implements Serializable {
    public float x;
    public float y;

    public OLVector2f() {
        x = 0;
        y = 0;
    }

    public OLVector2f(OLVector2f other) {
        this.x = other.x;
        this.y = other.y;
    }

    public OLVector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setOLVector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setOLVector2f(OLVector2f v) {
        x = v.x;
        y = v.y;
    }

    public OLVector2f negate() {
        return new OLVector2f(-x, -y);
    }


    public OLVector2f sub(OLVector2f v) {
        return new OLVector2f(x - v.x, y - v.y);
    }

    public float cross(OLVector2f v) {
        return x * v.y - y * v.x;
    }

    public float dot(OLVector2f v) {
        return x * v.x + y * v.y;
    }

    public float lengthSquared() {
        return x * x + y * y;
    }

    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }

    public float distance(OLVector2f v) {
        float dx = x - v.x;
        float dy = y - v.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public float distanceSquared(OLVector2f v) {
        float dx = this.x - v.x;
        float dy = this.y - v.y;
        return dx * dx + dy * dy;
    }

    public OLVector2f normalize() {
        float scalar = length();
        return new OLVector2f(x / scalar, y / scalar);
    }


    public OLVector2f add(OLVector2f v) {
        return new OLVector2f(x + v.x, y + v.y);
    }

    public OLVector2f mul(float scalar) {
        return new OLVector2f(x * scalar, y * scalar);
    }

    public OLVector2f min(OLVector2f v) {
        return new OLVector2f(Math.min(x, v.x), Math.min(y, v.y));
    }

    public OLVector2f max(OLVector2f v) {
        this.x = Math.max(x, v.x);
        this.y = Math.max(y, v.y);
        return new OLVector2f(x, y);
    }

    public OLVector2f absolute() {
        return new OLVector2f(Math.abs(x), Math.abs(y));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OLVector2f vector2f = (OLVector2f) o;

        return (x == vector2f.x) && (y == vector2f.y);
    }

    @Override
    public String toString() {
        return "OLVector2f{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}