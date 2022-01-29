package app.math.components;

import app.math.OLMatrix4f;
import app.math.OLVector3f;

public class Camera {
    OLVector3f position;
    OLVector3f rotation;
    OLVector3f negativeCameraPosition = new OLVector3f();
    OLMatrix4f viewMatrix;
    OLMatrix4f projectionMatrix;

    public Camera() {
        position = new OLVector3f(0.5f, 7f, 13);
        rotation = new OLVector3f(30, 0, 0);
        viewMatrix = new OLMatrix4f();
        projectionMatrix = new OLMatrix4f();
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

        negativeCameraPosition.setOLVector3f(-position.x, -position.y, -position.z);
        viewMatrix.translate(negativeCameraPosition);
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

    public void setViewMatrix(OLMatrix4f viewMatrix) {
        this.viewMatrix = viewMatrix;
    }

    public void setProjectionMatrix(OLMatrix4f projectionMatrix) {
        this.projectionMatrix = projectionMatrix;
    }
}
