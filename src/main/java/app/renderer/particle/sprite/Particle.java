package app.renderer.particle.sprite;

import app.math.OLVector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class Particle {
    private OLVector3f position;
    private float gravityEffect;
    private final float[] positions;
    private final FloatBuffer particleBuffer;
    private final int size;

    public Particle(OLVector3f position, float gravityEffect, int size) {
        this.position = position;
        this.gravityEffect = gravityEffect;
        positions = new float[size * 3];
        particleBuffer = BufferUtils.createFloatBuffer(size * 3);
        this.size = size;
        init();
    }

    private void init() {
        for (int i = 0; i < positions.length / 3; i++) {
            positions[i * 3] = position.x;
            positions[i * 3 + 1] = position.y;
            positions[i * 3 + 2] = position.z;
        }
        particleBuffer.put(positions);
        particleBuffer.flip();
    }

    public void update(float dt) {
        for (int i = 0; i < positions.length / 3; i++) {
            positions[i * 3] = getRandomNumber(position.x - 2, position.x + 2) * dt * gravityEffect;
            positions[i * 3 + 1] = getRandomNumber(position.y - 2, position.y + 2) * dt * gravityEffect;
            positions[i * 3 + 2] = getRandomNumber(position.z - 2, position.z + 2) * dt * gravityEffect;
        }
    }

    private float getRandomNumber(float min, float max) {
        return (float) ((Math.random() * (max - min)) + min);
    }

    public FloatBuffer getParticleBuffer() {
        return particleBuffer;
    }

    public int getSize() {
        return size;
    }

    public float[] getPositions() {
        return positions;
    }

    public OLVector3f getPosition() {
        return position;
    }

    public void setPosition(OLVector3f position) {
        this.position = position;
    }

}
