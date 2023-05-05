package app.math;

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
}
