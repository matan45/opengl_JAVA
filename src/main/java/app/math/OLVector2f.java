package app.math;

public class OLVector2f {
    public float x;
    public float y;

    public OLVector2f() {
        this.x = 0;
        this.y = 0;
    }

    public OLVector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setOLVector2f(OLVector2f v) {
        this.x = v.x;
        this.y = v.y;
    }

    public OLVector2f getOLVector2f() {
        return this;
    }

    public OLVector2f sub(OLVector2f v) {
        this.x = x - v.x;
        this.y = y - v.y;
        return this;
    }

    public float cross(OLVector2f v) {
        return x * v.y - y * v.x;
    }

    public float dot(OLVector2f v) {
        return x * v.x + y * v.y;
    }

    public float angle(OLVector2f v) {
        float dot = x * v.x + y * v.y;
        float det = x * v.y - y * v.x;
        return (float) Math.atan2(det, dot);
    }

    public float lengthSquared() {
        return x * x + y * y;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public float distance(OLVector2f v) {
        float dx = this.x - v.x;
        float dy = this.y - v.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public float distanceSquared(OLVector2f v) {
        float dx = this.x - v.x;
        float dy = this.y - v.y;
        return dx * dx + dy * dy;
    }

    public OLVector2f normalize() {
        float invLength = MathUtil.invsqrt(x * x + y * y);
        this.x = x * invLength;
        this.y = y * invLength;
        return this;
    }

    public OLVector2f add(OLVector2f v) {
        this.x = x + v.x;
        this.y = y + v.y;
        return this;
    }

    public OLVector2f mul(float scalar) {
        this.x = x * scalar;
        this.y = y * scalar;
        return this;
    }

    public OLVector2f div(OLVector2f v) {
        this.x = this.x / v.x;
        this.y = this.y / v.y;
        return this;
    }

    public OLVector2f div(float scalar) {
        float inv = 1.0f / scalar;
        this.x = this.x * inv;
        this.y = this.y * inv;
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
    public String toString() {
        return "OLVector2f{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
