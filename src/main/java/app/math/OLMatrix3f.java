package app.math;

public class OLMatrix3f {
    public float m00, m01, m02;
    public float m10, m11, m12;
    public float m20, m21, m22;

    public OLMatrix3f() {
        m00 = 1.0f;
        m11 = 1.0f;
        m22 = 1.0f;
    }

    public OLMatrix3f(float m00, float m01, float m02,
                      float m10, float m11, float m12,
                      float m20, float m21, float m22) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
    }

    public OLMatrix3f set(OLQuaternion4f q) {
        float w2 = q.w * q.w;
        float x2 = q.x * q.x;
        float y2 = q.y * q.y;
        float z2 = q.z * q.z;
        float zw = q.z * q.w;
        float xy = q.x * q.y;
        float xz = q.x * q.z;
        float yw = q.y * q.w;
        float yz = q.y * q.z;
        float xw = q.x * q.w;
        m00 = (w2 + x2 - z2 - y2);
        m01 = (xy + zw + zw + xy);
        m02 = (xz - yw + xz - yw);
        m10 = (-zw + xy - zw + xy);
        m11 = (y2 - z2 + w2 - x2);
        m12 = (yz + yz + xw + xw);
        m20 = (yw + xz + xz + yw);
        m21 = (yz + yz - xw - xw);
        m22 = (z2 - y2 - x2 + w2);
        return this;
    }

    public OLMatrix3f mul(OLMatrix3f right) {
        float nm00 = Math.fma(m00, right.m00, Math.fma(m10, right.m01, m20 * right.m02));
        float nm01 = Math.fma(m01, right.m00, Math.fma(m11, right.m01, m21 * right.m02));
        float nm02 = Math.fma(m02, right.m00, Math.fma(m12, right.m01, m22 * right.m02));
        float nm10 = Math.fma(m00, right.m10, Math.fma(m10, right.m11, m20 * right.m12));
        float nm11 = Math.fma(m01, right.m10, Math.fma(m11, right.m11, m21 * right.m12));
        float nm12 = Math.fma(m02, right.m10, Math.fma(m12, right.m11, m22 * right.m12));
        float nm20 = Math.fma(m00, right.m20, Math.fma(m10, right.m21, m20 * right.m22));
        float nm21 = Math.fma(m01, right.m20, Math.fma(m11, right.m21, m21 * right.m22));
        float nm22 = Math.fma(m02, right.m20, Math.fma(m12, right.m21, m22 * right.m22));
        this.m00 = nm00;
        this.m01 = nm01;
        this.m02 = nm02;
        this.m10 = nm10;
        this.m11 = nm11;
        this.m12 = nm12;
        this.m20 = nm20;
        this.m21 = nm21;
        this.m22 = nm22;
        return this;
    }

    public OLMatrix3f invert() {
        float a = Math.fma(m00, m11, -m01 * m10);
        float b = Math.fma(m02, m10, -m00 * m12);
        float c = Math.fma(m01, m12, -m02 * m11);
        float d = Math.fma(a, m22, Math.fma(b, m21, c * m20));
        float s = 1.0f / d;
        float nm00 = Math.fma(m11, m22, -m21 * m12) * s;
        float nm01 = Math.fma(m21, m02, -m01 * m22) * s;
        float nm02 = c * s;
        float nm10 = Math.fma(m20, m12, -m10 * m22) * s;
        float nm11 = Math.fma(m00, m22, -m20 * m02) * s;
        float nm12 = b * s;
        float nm20 = Math.fma(m10, m21, -m20 * m11) * s;
        float nm21 = Math.fma(m20, m01, -m00 * m21) * s;
        float nm22 = a * s;
        this.m00 = nm00;
        this.m01 = nm01;
        this.m02 = nm02;
        this.m10 = nm10;
        this.m11 = nm11;
        this.m12 = nm12;
        this.m20 = nm20;
        this.m21 = nm21;
        this.m22 = nm22;
        return this;
    }

    public OLMatrix3f transpose() {
        return this.set(m00, m10, m20,
                m01, m11, m21,
                m02, m12, m22);
    }

    public OLMatrix3f set(float m00, float m01, float m02,
                          float m10, float m11, float m12,
                          float m20, float m21, float m22
    ) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        return this;

    }

    public OLMatrix3f scale(OLVector3f v) {
        this.m00 = m00 * v.x;
        this.m01 = m01 * v.x;
        this.m02 = m02 * v.x;
        this.m10 = m10 * v.y;
        this.m11 = m11 * v.y;
        this.m12 = m12 * v.y;
        this.m20 = m20 * v.z;
        this.m21 = m21 * v.z;
        this.m22 = m22 * v.z;
        return this;
    }

    public OLMatrix3f rotation(OLQuaternion4f quat) {
        float w2 = quat.w * quat.w;
        float x2 = quat.x * quat.x;
        float y2 = quat.y * quat.y;
        float z2 = quat.z * quat.z;
        float zw = quat.z * quat.w, dzw = zw + zw;
        float xy = quat.x * quat.y, dxy = xy + xy;
        float xz = quat.x * quat.z, dxz = xz + xz;
        float yw = quat.y * quat.w, dyw = yw + yw;
        float yz = quat.y * quat.z, dyz = yz + yz;
        float xw = quat.x * quat.w, dxw = xw + xw;
        m00 = w2 + x2 - z2 - y2;
        m01 = dxy + dzw;
        m02 = dxz - dyw;
        m10 = -dzw + dxy;
        m11 = y2 - z2 + w2 - x2;
        m12 = dyz + dxw;
        m20 = dyw + dxz;
        m21 = dyz - dxw;
        m22 = z2 - y2 - x2 + w2;
        return this;
    }

    public OLVector3f transform(OLVector3f v) {
        return v.mul(this);
    }


    public OLMatrix3f add(OLMatrix3f other) {
        this.m00 = m00 + other.m00;
        this.m01 = m01 + other.m01;
        this.m02 = m02 + other.m02;
        this.m10 = m10 + other.m10;
        this.m11 = m11 + other.m11;
        this.m12 = m12 + other.m12;
        this.m20 = m20 + other.m20;
        this.m21 = m21 + other.m21;
        this.m22 = m22 + other.m22;
        return this;
    }

    public OLMatrix3f sub(OLMatrix3f subtrahend) {
        this.m00 = m00 - subtrahend.m00;
        this.m01 = m01 - subtrahend.m01;
        this.m02 = m02 - subtrahend.m02;
        this.m10 = m10 - subtrahend.m10;
        this.m11 = m11 - subtrahend.m11;
        this.m12 = m12 - subtrahend.m12;
        this.m20 = m20 - subtrahend.m20;
        this.m21 = m21 - subtrahend.m21;
        this.m22 = m22 - subtrahend.m22;
        return this;
    }

    public OLMatrix3f normal() {
        float m00m11 = m00 * m11;
        float m01m10 = m01 * m10;
        float m02m10 = m02 * m10;
        float m00m12 = m00 * m12;
        float m01m12 = m01 * m12;
        float m02m11 = m02 * m11;
        float det = (m00m11 - m01m10) * m22 + (m02m10 - m00m12) * m21 + (m01m12 - m02m11) * m20;
        float s = 1.0f / det;
        /* Invert and transpose in one go */
        float nm00 = (m11 * m22 - m21 * m12) * s;
        float nm01 = (m20 * m12 - m10 * m22) * s;
        float nm02 = (m10 * m21 - m20 * m11) * s;
        float nm10 = (m21 * m02 - m01 * m22) * s;
        float nm11 = (m00 * m22 - m20 * m02) * s;
        float nm12 = (m20 * m01 - m00 * m21) * s;
        float nm20 = (m01m12 - m02m11) * s;
        float nm21 = (m02m10 - m00m12) * s;
        float nm22 = (m00m11 - m01m10) * s;
        this.m00 = nm00;
        this.m01 = nm01;
        this.m02 = nm02;
        this.m10 = nm10;
        this.m11 = nm11;
        this.m12 = nm12;
        this.m20 = nm20;
        this.m21 = nm21;
        this.m22 = nm22;
        return this;
    }
}
