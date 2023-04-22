package app.math.components;

import app.math.OLMatrix4f;
import app.math.OLVector2f;
import app.math.OLVector3f;
import app.renderer.OpenGLObjects;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;

public class Camera {
    private final OLVector3f position;
    private final OLVector3f rotation;
    private final OLVector3f negativeCameraPosition;
    private final OLMatrix4f viewMatrix;
    private final OLMatrix4f projectionMatrix;
    private OLVector2f viewPort;
    private float speed;
    private float far;
    private float near;
    private float aspect;
    private final int uboMatrices;
    private static final FloatBuffer viewBuffer = BufferUtils.createFloatBuffer(4 * 4);
    private static final FloatBuffer projectionBuffer = BufferUtils.createFloatBuffer(4 * 4);

    public Camera(OpenGLObjects openGLObjects) {
        position = new OLVector3f(0.0f, 25f, 51f);
        rotation = new OLVector3f(34, 0, 0);
        viewMatrix = new OLMatrix4f();
        projectionMatrix = new OLMatrix4f();
        negativeCameraPosition = new OLVector3f();
        viewPort = new OLVector2f();
        speed = 20f;
        near = 0.1f;
        far = 2048f;
        aspect = 1f;
        uboMatrices = openGLObjects.createUniformBufferFloat(16, 2L);
    }

    public void createPerspectiveMatrix(float fovY) {
        float yScale = (float) (1f / Math.tan(Math.toRadians(fovY / 2f)));
        float xScale = yScale / aspect;
        float frustumLength = far - near;
        projectionMatrix.m00 = xScale;
        projectionMatrix.m11 = yScale;
        projectionMatrix.m22 = -((far + near) / frustumLength);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * near * far) / frustumLength);
        projectionMatrix.m33 = 0;
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

    public void updateMatrices() {
        projectionBuffer.clear();
        projectionBuffer.put(projectionMatrix.getAsArray());
        projectionBuffer.flip();
        viewBuffer.clear();
        viewBuffer.put(viewMatrix.getAsArray());
        viewBuffer.flip();
        glBindBuffer(GL_UNIFORM_BUFFER, uboMatrices);
        glBufferSubData(GL_UNIFORM_BUFFER, 0, projectionBuffer);
        glBufferSubData(GL_UNIFORM_BUFFER, 64, viewBuffer);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }

    public OLVector3f getPosition() {
        return position;
    }

    public OLVector3f getRotation() {
        return rotation;
    }

    public OLMatrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public OLVector2f getViewPort() {
        return viewPort;
    }

    public void setViewPort(OLVector2f viewPort) {
        this.viewPort = viewPort;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getFar() {
        return far;
    }

    public void setFar(float far) {
        this.far = far;
        createPerspectiveMatrix(70f);
    }

    public float getNear() {
        return near;
    }

    public void setNear(float near) {
        this.near = near;
        createPerspectiveMatrix(70f);
    }

    public void setAspect(float aspect) {
        this.aspect = aspect;
    }
}