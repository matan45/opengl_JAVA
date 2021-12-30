package app.math;

public class OLMatrix4f {

    float m00, m01, m02, m03;
    float m10, m11, m12, m13;
    float m20, m21, m22, m23;
    float m30, m31, m32, m33;

    public OLMatrix4f() {
        this.m00 = 1.0f;
        this.m11 = 1.0f;
        this.m22 = 1.0f;
        this.m33 = 1.0f;
    }

    public OLMatrix4f identity() {
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m03 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = 1.0f;
        this.m12 = 0.0f;
        this.m13 = 0.0f;
        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = 1.0f;
        this.m23 = 0.0f;
        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 1.0f;
        return this;
    }

    public OLMatrix4f mul(OLMatrix4f right) {
        float nm00 = Math.fma(m00, right.m00, Math.fma(m10, right.m01, Math.fma(m20, right.m02, m30 * right.m03)));
        float nm01 = Math.fma(m01, right.m00, Math.fma(m11, right.m01, Math.fma(m21, right.m02, m31 * right.m03)));
        float nm02 = Math.fma(m02, right.m00, Math.fma(m12, right.m01, Math.fma(m22, right.m02, m32 * right.m03)));
        float nm03 = Math.fma(m03, right.m00, Math.fma(m13, right.m01, Math.fma(m23, right.m02, m33 * right.m03)));
        float nm10 = Math.fma(m00, right.m10, Math.fma(m10, right.m11, Math.fma(m20, right.m12, m30 * right.m13)));
        float nm11 = Math.fma(m01, right.m10, Math.fma(m11, right.m11, Math.fma(m21, right.m12, m31 * right.m13)));
        float nm12 = Math.fma(m02, right.m10, Math.fma(m12, right.m11, Math.fma(m22, right.m12, m32 * right.m13)));
        float nm13 = Math.fma(m03, right.m10, Math.fma(m13, right.m11, Math.fma(m23, right.m12, m33 * right.m13)));
        float nm20 = Math.fma(m00, right.m20, Math.fma(m10, right.m21, Math.fma(m20, right.m22, m30 * right.m23)));
        float nm21 = Math.fma(m01, right.m20, Math.fma(m11, right.m21, Math.fma(m21, right.m22, m31 * right.m23)));
        float nm22 = Math.fma(m02, right.m20, Math.fma(m12, right.m21, Math.fma(m22, right.m22, m32 * right.m23)));
        float nm23 = Math.fma(m03, right.m20, Math.fma(m13, right.m21, Math.fma(m23, right.m22, m33 * right.m23)));
        float nm30 = Math.fma(m00, right.m30, Math.fma(m10, right.m31, Math.fma(m20, right.m32, m30 * right.m33)));
        float nm31 = Math.fma(m01, right.m30, Math.fma(m11, right.m31, Math.fma(m21, right.m32, m31 * right.m33)));
        float nm32 = Math.fma(m02, right.m30, Math.fma(m12, right.m31, Math.fma(m22, right.m32, m32 * right.m33)));
        float nm33 = Math.fma(m03, right.m30, Math.fma(m13, right.m31, Math.fma(m23, right.m32, m33 * right.m33)));
        this.m00 = nm00;
        this.m01 = nm01;
        this.m02 = nm02;
        this.m03 = nm03;
        this.m10 = nm10;
        this.m11 = nm11;
        this.m12 = nm12;
        this.m13 = nm13;
        this.m20 = nm20;
        this.m21 = nm21;
        this.m22 = nm22;
        this.m23 = nm23;
        this.m30 = nm30;
        this.m31 = nm31;
        this.m32 = nm32;
        this.m33 = nm33;
        return this;
    }

    public OLMatrix4f add(OLMatrix4f other) {
        this.m00 = m00 + other.m00;
        this.m01 = m01 + other.m01;
        this.m02 = m02 + other.m02;
        this.m03 = m03 + other.m03;
        this.m10 = m10 + other.m10;
        this.m11 = m11 + other.m11;
        this.m12 = m12 + other.m12;
        this.m13 = m13 + other.m13;
        this.m20 = m20 + other.m20;
        this.m21 = m21 + other.m21;
        this.m22 = m22 + other.m22;
        this.m23 = m23 + other.m23;
        this.m30 = m30 + other.m30;
        this.m31 = m31 + other.m31;
        this.m32 = m32 + other.m32;
        this.m33 = m33 + other.m33;

        return this;
    }

    public OLMatrix4f sub(OLMatrix4f other) {
        this.m00 = m00 - other.m00;
        this.m01 = m01 - other.m01;
        this.m02 = m02 - other.m02;
        this.m03 = m03 - other.m03;
        this.m10 = m10 - other.m10;
        this.m11 = m11 - other.m11;
        this.m12 = m12 - other.m12;
        this.m13 = m13 - other.m13;
        this.m20 = m20 - other.m20;
        this.m21 = m21 - other.m21;
        this.m22 = m22 - other.m22;
        this.m23 = m23 - other.m23;
        this.m30 = m30 - other.m30;
        this.m31 = m31 - other.m31;
        this.m32 = m32 - other.m32;
        this.m33 = m33 - other.m33;
        return this;
    }
}
