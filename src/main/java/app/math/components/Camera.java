package app.math.components;

import app.math.OLMatrix4f;
import app.math.OLVector3f;

public class Camera {
    OLVector3f position;
    OLVector3f rotation;
    OLVector3f negativeCameraPosition = new OLVector3f();
    OLMatrix4f viewMatrix;
    OLMatrix4f projectionMatrix;

    OLVector3f lookAtTemp;

    public Camera() {
        position = new OLVector3f(0, 1, 0);
        rotation = new OLVector3f(0, 0, 0);
        viewMatrix = new OLMatrix4f();
        projectionMatrix = new OLMatrix4f();
        lookAtTemp = new OLVector3f();
    }

    public OLMatrix4f createPerspectiveMatrix(float fovY, float aspect, float near, float far) {

        float yScale = (float) (near * Math.tan(fovY * Math.PI / 180.0f));
        float xScale = yScale * aspect;

        return frustum(-xScale, xScale, -yScale, yScale, near, far);
    }

    private OLMatrix4f frustum(float left, float right, float bottom, float top, float near, float far) {

        float temp = 2.0f * near;
        float temp2 = right - left;
        float temp3 = top - bottom;
        float temp4 = far - near;
        projectionMatrix.m00 = temp / temp2;
        projectionMatrix.m01 = 0.0f;
        projectionMatrix.m02 = 0.0f;
        projectionMatrix.m03 = 0.0f;
        projectionMatrix.m10 = 0.0f;
        projectionMatrix.m11 = temp / temp3;
        projectionMatrix.m12 = 0.0f;
        projectionMatrix.m13 = 0.0f;
        projectionMatrix.m20 = (right + left) / temp2;
        projectionMatrix.m21 = (top + bottom) / temp3;
        projectionMatrix.m22 = (-far - near) / temp4;
        projectionMatrix.m23 = -1.0f;
        projectionMatrix.m30 = 0.0f;
        projectionMatrix.m31 = 0.0f;
        projectionMatrix.m32 = (-temp * far) / temp4;
        projectionMatrix.m33 = 0.0f;

        return projectionMatrix;
    }

    public OLMatrix4f createViewMatrix() {
        viewMatrix.identity();
        //Pitch
        viewMatrix.rotate((float) Math.toRadians(rotation.x), OLVector3f.Xaxis);
        //Yaw
        viewMatrix.rotate((float) Math.toRadians(rotation.y), OLVector3f.Yaxis);
        //Roll
        viewMatrix.rotate((float) Math.toRadians(rotation.z), OLVector3f.Zaxis);
        negativeCameraPosition.x = -position.x;
        negativeCameraPosition.y = -position.y;
        negativeCameraPosition.z = -position.z;
        viewMatrix.translate(negativeCameraPosition);
        return viewMatrix;
    }

    public OLMatrix4f lookAt(OLVector3f eye) {
        OLVector3f x;
        OLVector3f y;
        OLVector3f z;

        lookAtTemp.x = eye.x - position.x;
        lookAtTemp.y = eye.y - position.y;
        lookAtTemp.z = eye.z - position.z;

        z = lookAtTemp.normalize();
        y = OLVector3f.Yaxis;

        lookAtTemp = y.cross(z);
        x = lookAtTemp.normalize();

        lookAtTemp = z.cross(x);
        y = lookAtTemp.normalize();

        viewMatrix.m00 = x.x;
        viewMatrix.m01 = y.x;
        viewMatrix.m02 = z.x;
        viewMatrix.m03 = 0.0f;
        viewMatrix.m10 = x.y;
        viewMatrix.m11 = y.y;
        viewMatrix.m12 = z.y;
        viewMatrix.m13 = 0.0f;
        viewMatrix.m20 = x.z;
        viewMatrix.m21 = y.z;
        viewMatrix.m22 = z.z;
        viewMatrix.m23 = 0.0f;
        viewMatrix.m30 = -x.dot(eye);
        viewMatrix.m31 = -y.dot(eye);
        viewMatrix.m32 = -z.dot(eye);
        viewMatrix.m33 = 1.0f;

        return viewMatrix;
    }

    public OLVector3f getPosition() {
        return position;
    }

    public OLVector3f getRotation() {
        return rotation;
    }

    public OLMatrix4f getViewMatrix() {
        return viewMatrix;
    }

    public OLMatrix4f getProjectionMatrix() {
        return projectionMatrix;
    }
}
