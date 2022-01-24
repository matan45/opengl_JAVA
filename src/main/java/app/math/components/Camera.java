package app.math.components;

import app.math.OLMatrix4f;
import app.math.OLVector3f;

public class Camera {
    OLVector3f position;
    OLVector3f rotation;
    OLVector3f negativeCameraPosition;
    OLMatrix4f viewMatrix;
    OLMatrix4f projectionMatrix;

    public Camera() {
        this.position = new OLVector3f();
        this.rotation = new OLVector3f();
        viewMatrix = new OLMatrix4f();
        projectionMatrix = new OLMatrix4f();
    }

    public OLMatrix4f createPerspectiveMatrix(float fovY, float aspect, float near, float far) {

        float y_scale = (float) (1f / Math.tan(Math.toRadians(fovY / 2f)));
        float x_scale = y_scale / aspect;
        float frustum_length = far - near;

        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((far - near) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * near * far) / frustum_length);
        projectionMatrix.m33 = 0;
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
        negativeCameraPosition = position.mul(-1.0f);
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
}
