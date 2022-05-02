package app.math;

import java.io.Serializable;
import java.util.Objects;

public class OLVector2f implements Serializable {
    public float x;
    public float y;

    public OLVector2f() {
        x = 0;
        y = 0;
    }

    public OLVector2f(OLVector2f other) {
        x = other.x;
        y = other.y;
    }

    public OLVector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public OLVector2f setOLVector2f(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public OLVector2f setOLVector2f(OLVector2f v) {
        x = v.x;
        y = v.y;
        return this;
    }

    public OLVector2f negate() {
        x = -x;
        y = -y;
        return this;
    }


    public OLVector2f sub(OLVector2f v) {
        x -= v.x;
        y -= v.y;
        return this;
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
        float invLength = length();
        this.x = x * invLength;
        this.y = y * invLength;
        return this;
    }


    public OLVector2f add(OLVector2f v) {
        x += v.x;
        y += v.y;
        return this;
    }

    public OLVector2f mul(float scalar) {
        x *= scalar;
        y *= scalar;
        return this;
    }

    public OLVector2f min(OLVector2f v) {
        this.x = Math.min(x, v.x);
        this.y = Math.min(y, v.y);
        return this;
    }

    public OLVector2f max(OLVector2f v) {
        this.x = Math.max(x, v.x);
        this.y = Math.max(y, v.y);
        return this;
    }

    public OLVector2f absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OLVector2f vector2f = (OLVector2f) o;

        return (x == vector2f.x) && (y == vector2f.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this);
    }

    @Override
    public String toString() {
        return "OLVector2f{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
