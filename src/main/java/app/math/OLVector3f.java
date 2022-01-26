package app.math;

public class OLVector3f {
    public float x;
    public float y;
    public float z;

    public static final OLVector3f Xaxis = new OLVector3f(1, 0, 0);
    public static final OLVector3f Yaxis = new OLVector3f(0, 1, 0);
    public static final OLVector3f Zaxis = new OLVector3f(0, 0, 1);

    public OLVector3f() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public OLVector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setOLVector3f(OLVector3f v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public OLVector3f getOLVector3f() {
        return this;
    }

    public OLVector3f sub(OLVector3f v) {
        this.x = x - v.x;
        this.y = y - v.y;
        this.z = z - v.z;
        return this;
    }

    public OLVector3f add(OLVector3f v) {
        this.x = this.x + v.x;
        this.y = this.y + v.y;
        this.z = this.z + v.z;
        return this;
    }

    public OLVector3f mulAdd(OLVector3f a, OLVector3f b) {
        this.x = Math.fma(x, a.x, b.x);
        this.y = Math.fma(y, a.y, b.y);
        this.z = Math.fma(z, a.z, b.z);
        return this;
    }

    public OLVector3f mul(OLVector3f v) {
        this.x = x * v.x;
        this.y = y * v.y;
        this.z = z * v.z;
        return this;
    }

    public OLVector3f mul(float scalar) {
        x = x * scalar;
        y = y * scalar;
        z = z * scalar;
        return this;
    }

    public OLVector3f div(OLVector3f v) {
        this.x = this.x / v.x;
        this.y = this.y / v.y;
        this.z = this.z / v.z;
        return this;
    }

    public float lengthSquared() {
        return Math.fma(x, x, Math.fma(y, y, z * z));
    }

    public float length() {
        return (float) Math.sqrt(Math.fma(x, x, Math.fma(y, y, z * z)));
    }

    public OLVector3f normalize() {
        float scalar = MathUtil.invsqrt(Math.fma(x, x, Math.fma(y, y, z * z)));
        this.x = this.x * scalar;
        this.y = this.y * scalar;
        this.z = this.z * scalar;
        return this;
    }

    public OLVector3f cross(OLVector3f v) {
        float rx = Math.fma(y, v.z, -z * v.y);
        float ry = Math.fma(z, v.x, -x * v.z);
        float rz = Math.fma(x, v.y, -y * v.x);
        this.x = rx;
        this.y = ry;
        this.z = rz;
        return this;
    }

    public float distance(OLVector3f v) {
        float dx = this.x - v.x;
        float dy = this.y - v.y;
        float dz = this.z - v.z;
        return (float) Math.sqrt(Math.fma(dx, dx, Math.fma(dy, dy, dz * dz)));
    }

    public float distanceSquared(OLVector3f v) {
        float dx = this.x - v.x;
        float dy = this.y - v.y;
        float dz = this.z - v.z;
        return Math.fma(dx, dx, Math.fma(dy, dy, dz * dz));
    }

    public OLVector3f min(OLVector3f v) {
        float x = this.x, y = this.y, z = this.z;
        this.x = Math.min(x, v.x);
        this.y = Math.min(y, v.y);
        this.z = Math.min(z, v.z);
        return this;
    }


    public OLVector3f max(OLVector3f v) {
        float x = this.x, y = this.y, z = this.z;
        this.x = Math.max(x, v.x);
        this.y = Math.max(y, v.y);
        this.z = Math.max(z, v.z);
        return this;
    }

    public OLVector3f absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        this.z = Math.abs(this.z);
        return this;
    }

    public float dot(OLVector3f v) {
        return Math.fma(this.x, v.x, Math.fma(this.y, v.y, this.z * v.z));
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
