package app.math;

import java.nio.FloatBuffer;

public class OLMatrix4f {

    public float m00, m01, m02, m03;
    public float m10, m11, m12, m13;
    public float m20, m21, m22, m23;
    public float m30, m31, m32, m33;

    float[] matrixArray = new float[4 * 4];

    public OLMatrix4f() {
        identity();
    }

    public void identity() {
        m00 = 1.0f;
        m01 = 0.0f;
        m02 = 0.0f;
        m03 = 0.0f;
        m10 = 0.0f;
        m11 = 1.0f;
        m12 = 0.0f;
        m13 = 0.0f;
        m20 = 0.0f;
        m21 = 0.0f;
        m22 = 1.0f;
        m23 = 0.0f;
        m30 = 0.0f;
        m31 = 0.0f;
        m32 = 0.0f;
        m33 = 1.0f;
    }

    public OLMatrix4f mul(OLMatrix4f right) {

        float nm00 = m00 * right.m00 + m10 * right.m01 + m20 * right.m02 + m30 * right.m03;
        float nm01 = m01 * right.m00 + m11 * right.m01 + m21 * right.m02 + m31 * right.m03;
        float nm02 = m02 * right.m00 + m12 * right.m01 + m22 * right.m02 + m32 * right.m03;
        float nm03 = m03 * right.m00 + m13 * right.m01 + m23 * right.m02 + m33 * right.m03;
        float nm10 = m00 * right.m10 + m10 * right.m11 + m20 * right.m12 + m30 * right.m13;
        float nm11 = m01 * right.m10 + m11 * right.m11 + m21 * right.m12 + m31 * right.m13;
        float nm12 = m02 * right.m10 + m12 * right.m11 + m22 * right.m12 + m32 * right.m13;
        float nm13 = m03 * right.m10 + m13 * right.m11 + m23 * right.m12 + m33 * right.m13;
        float nm20 = m00 * right.m20 + m10 * right.m21 + m20 * right.m22 + m30 * right.m23;
        float nm21 = m01 * right.m20 + m11 * right.m21 + m21 * right.m22 + m31 * right.m23;
        float nm22 = m02 * right.m20 + m12 * right.m21 + m22 * right.m22 + m32 * right.m23;
        float nm23 = m03 * right.m20 + m13 * right.m21 + m23 * right.m22 + m33 * right.m23;
        float nm30 = m00 * right.m30 + m10 * right.m31 + m20 * right.m32 + m30 * right.m33;
        float nm31 = m01 * right.m30 + m11 * right.m31 + m21 * right.m32 + m31 * right.m33;
        float nm32 = m02 * right.m30 + m12 * right.m31 + m22 * right.m32 + m32 * right.m33;
        float nm33 = m03 * right.m30 + m13 * right.m31 + m23 * right.m32 + m33 * right.m33;
        m00 = nm00;
        m01 = nm01;
        m02 = nm02;
        m03 = nm03;
        m10 = nm10;
        m11 = nm11;
        m12 = nm12;
        m13 = nm13;
        m20 = nm20;
        m21 = nm21;
        m22 = nm22;
        m23 = nm23;
        m30 = nm30;
        m31 = nm31;
        m32 = nm32;
        m33 = nm33;
        return this;
    }

    public OLMatrix4f add(OLMatrix4f other) {
        m00 = m00 + other.m00;
        m01 = m01 + other.m01;
        m02 = m02 + other.m02;
        m03 = m03 + other.m03;
        m10 = m10 + other.m10;
        m11 = m11 + other.m11;
        m12 = m12 + other.m12;
        m13 = m13 + other.m13;
        m20 = m20 + other.m20;
        m21 = m21 + other.m21;
        m22 = m22 + other.m22;
        m23 = m23 + other.m23;
        m30 = m30 + other.m30;
        m31 = m31 + other.m31;
        m32 = m32 + other.m32;
        m33 = m33 + other.m33;

        return this;
    }

    public OLMatrix4f sub(OLMatrix4f other) {
        m00 = m00 - other.m00;
        m01 = m01 - other.m01;
        m02 = m02 - other.m02;
        m03 = m03 - other.m03;
        m10 = m10 - other.m10;
        m11 = m11 - other.m11;
        m12 = m12 - other.m12;
        m13 = m13 - other.m13;
        m20 = m20 - other.m20;
        m21 = m21 - other.m21;
        m22 = m22 - other.m22;
        m23 = m23 - other.m23;
        m30 = m30 - other.m30;
        m31 = m31 - other.m31;
        m32 = m32 - other.m32;
        m33 = m33 - other.m33;
        return this;
    }

    public float[] getAsArray() {
        matrixArray[0] = m00;
        matrixArray[1] = m01;
        matrixArray[2] = m02;
        matrixArray[3] = m03;
        matrixArray[4] = m10;
        matrixArray[5] = m11;
        matrixArray[6] = m12;
        matrixArray[7] = m13;
        matrixArray[8] = m20;
        matrixArray[9] = m21;
        matrixArray[10] = m22;
        matrixArray[11] = m23;
        matrixArray[12] = m30;
        matrixArray[13] = m31;
        matrixArray[14] = m32;
        matrixArray[15] = m33;
        return matrixArray;
    }

    public void store(FloatBuffer buf) {
        buf.clear();
        buf.put(m00);
        buf.put(m01);
        buf.put(m02);
        buf.put(m03);
        buf.put(m10);
        buf.put(m11);
        buf.put(m12);
        buf.put(m13);
        buf.put(m20);
        buf.put(m21);
        buf.put(m22);
        buf.put(m23);
        buf.put(m30);
        buf.put(m31);
        buf.put(m32);
        buf.put(m33);
    }

    public void translate(OLVector3f translate) {

        m30 += m00 * translate.x + m10 * translate.y + m20 * translate.z;
        m31 += m01 * translate.x + m11 * translate.y + m21 * translate.z;
        m32 += m02 * translate.x + m12 * translate.y + m22 * translate.z;
        m33 += m03 * translate.x + m13 * translate.y + m23 * translate.z;
    }

    public void rotate(float angle, OLVector3f axis) {
        float c = (float) Math.cos(angle);
        float s = (float) Math.sin(angle);
        float oneminusc = 1.0f - c;
        float xy = axis.x * axis.y;
        float yz = axis.y * axis.z;
        float xz = axis.x * axis.z;
        float xs = axis.x * s;
        float ys = axis.y * s;
        float zs = axis.z * s;

        float f00 = axis.x * axis.x * oneminusc + c;
        float f01 = xy * oneminusc + zs;
        float f02 = xz * oneminusc - ys;
        // n[3] not used
        float f10 = xy * oneminusc - zs;
        float f11 = axis.y * axis.y * oneminusc + c;
        float f12 = yz * oneminusc + xs;
        // n[7] not used
        float f20 = xz * oneminusc + ys;
        float f21 = yz * oneminusc - xs;
        float f22 = axis.z * axis.z * oneminusc + c;

        float t00 = m00 * f00 + m10 * f01 + this.m20 * f02;
        float t01 = m01 * f00 + m11 * f01 + m21 * f02;
        float t02 = m02 * f00 + m12 * f01 + m22 * f02;
        float t03 = m03 * f00 + m13 * f01 + m23 * f02;
        float t10 = m00 * f10 + m10 * f11 + m20 * f12;
        float t11 = m01 * f10 + m11 * f11 + m21 * f12;
        float t12 = m02 * f10 + m12 * f11 + m22 * f12;
        float t13 = m03 * f10 + m13 * f11 + m23 * f12;
        m20 = m00 * f20 + m10 * f21 + m20 * f22;
        m21 = m01 * f20 + m11 * f21 + m21 * f22;
        m22 = m02 * f20 + m12 * f21 + m22 * f22;
        m23 = m03 * f20 + m13 * f21 + m23 * f22;
        m00 = t00;
        m01 = t01;
        m02 = t02;
        m03 = t03;
        m10 = t10;
        m11 = t11;
        m12 = t12;
        m13 = t13;
    }

    public void scale(OLVector3f scale) {
        m00 *= scale.x;
        m01 *= scale.x;
        m02 *= scale.x;
        m03 *= scale.x;
        m10 *= scale.y;
        m11 *= scale.y;
        m12 *= scale.y;
        m13 *= scale.y;
        m20 *= scale.z;
        m21 *= scale.z;
        m22 *= scale.z;
        m23 *= scale.z;
    }

    /*
     * @param eye
     *            the position of the camera
     * @param center
     *            the point in space to look at
     * @param up
     *            the direction of 'up'
     */
    public OLMatrix4f lookAt(OLVector3f eye, OLVector3f center, OLVector3f up) {
        // Compute direction from position to lookAt
        float dirX, dirY, dirZ;
        dirX = eye.x - center.x;
        dirY = eye.y - center.y;
        dirZ = eye.z - center.z;
        // Normalize direction
        float invDirLength = MathUtil.invsqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        dirX *= invDirLength;
        dirY *= invDirLength;
        dirZ *= invDirLength;
        // left = up x direction
        float leftX, leftY, leftZ;
        leftX = up.y * dirZ - up.z * dirY;
        leftY = up.z * dirX - up.x * dirZ;
        leftZ = up.x * dirY - up.y * dirX;
        // normalize left
        float invLeftLength = MathUtil.invsqrt(leftX * leftX + leftY * leftY + leftZ * leftZ);
        leftX *= invLeftLength;
        leftY *= invLeftLength;
        leftZ *= invLeftLength;
        // up = direction x left
        float upnX = dirY * leftZ - dirZ * leftY;
        float upnY = dirZ * leftX - dirX * leftZ;
        float upnZ = dirX * leftY - dirY * leftX;

        // calculate right matrix elements
        float rm30 = -(leftX * eye.x + leftY * eye.y + leftZ * eye.z);
        float rm31 = -(upnX * eye.x + upnY * eye.y + upnZ * eye.z);
        float rm32 = -(dirX * eye.x + dirY * eye.y + dirZ * eye.z);
        // introduce temporaries for dependent results
        float nm00 = m00 * leftX + m10 * upnX + m20 * dirX;
        float nm01 = m01 * leftX + m11 * upnX + m21 * dirX;
        float nm02 = m02 * leftX + m12 * upnX + m22 * dirX;
        float nm03 = m03 * leftX + m13 * upnX + m23 * dirX;
        float nm10 = m00 * leftY + m10 * upnY + m20 * dirY;
        float nm11 = m01 * leftY + m11 * upnY + m21 * dirY;
        float nm12 = m02 * leftY + m12 * upnY + m22 * dirY;
        float nm13 = m03 * leftY + m13 * upnY + m23 * dirY;

        m30 = m00 * rm30 + m10 * rm31 + m20 * rm32 + m30;
        m31 = m01 * rm30 + m11 * rm31 + m21 * rm32 + m31;
        m32 = m02 * rm30 + m12 * rm31 + m22 * rm32 + m32;
        m33 = m03 * rm30 + m13 * rm31 + m23 * rm32 + m33;
        m20 = m00 * leftZ + m10 * upnZ + m20 * dirZ;
        m21 = m01 * leftZ + m11 * upnZ + m21 * dirZ;
        m22 = m02 * leftZ + m12 * upnZ + m22 * dirZ;
        m23 = m03 * leftZ + m13 * upnZ + m23 * dirZ;
        m00 = nm00;
        m01 = nm01;
        m02 = nm02;
        m03 = nm03;
        m10 = nm10;
        m11 = nm11;
        m12 = nm12;
        m13 = nm13;

        // perform optimized matrix multiplication
        // compute last column first, because others do not depend on it
        return this;
    }


    @Override
    public String toString() {
        return "OLMatrix4f{" +
                "m00=" + m00 +
                ", m01=" + m01 +
                ", m02=" + m02 +
                ", m03=" + m03 +
                ", m10=" + m10 +
                ", m11=" + m11 +
                ", m12=" + m12 +
                ", m13=" + m13 +
                ", m20=" + m20 +
                ", m21=" + m21 +
                ", m22=" + m22 +
                ", m23=" + m23 +
                ", m30=" + m30 +
                ", m31=" + m31 +
                ", m32=" + m32 +
                ", m33=" + m33 +
                '}';
    }
}
