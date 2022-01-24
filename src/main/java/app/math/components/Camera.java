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
        position = new OLVector3f();
        rotation = new OLVector3f();
        viewMatrix = new OLMatrix4f();
        projectionMatrix = new OLMatrix4f();
        viewMatrix = createViewMatrix();

    }

    public OLMatrix4f createPerspectiveMatrix(float fovY, float aspect, float near, float far) {

        float yScale = (float) (1f / Math.tan(Math.toRadians(fovY / 2f)));
        float xScale = yScale / aspect;
        float frustumLength = far - near;

        projectionMatrix.m00 = xScale;
        projectionMatrix.m11 = yScale;
        projectionMatrix.m22 = -((far - near) / frustumLength);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * near * far) / frustumLength);
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
