package app.renderer.particle.mesh;

import app.math.MathUtil;
import app.math.OLMatrix4f;
import app.math.OLVector3f;

public class ParticleMesh {
    private OLVector3f position;
    private OLVector3f velocity;
    private OLVector3f rotation;
    private OLVector3f scale;
    private float lifeLength;
    private float elapsedTime;
    private float gravityEffect;
    private boolean infinity;
    
    public ParticleMesh(OLVector3f position, OLVector3f velocity, float gravityEffect, 
                       float lifeLength, OLVector3f rotation, OLVector3f scale, boolean infinity) {
        this.position = new OLVector3f(position);
        this.velocity = new OLVector3f(velocity);
        this.gravityEffect = gravityEffect;
        this.lifeLength = lifeLength;
        this.rotation = new OLVector3f(rotation);
        this.scale = new OLVector3f(scale);
        this.infinity = infinity;
        this.elapsedTime = 0;
    }
    
    public boolean update(float dt) {
        velocity = new OLVector3f(velocity.x, velocity.y + gravityEffect * dt, velocity.z);
        OLVector3f change = velocity.mul(dt);
        position = position.add(change);
        
        elapsedTime += dt;
        
        if (!infinity && elapsedTime >= lifeLength) {
            reset();
            return false;
        }
        
        return true;
    }
    
    public void reset() {
        elapsedTime = 0;
    }
    
    public OLMatrix4f getModelMatrix() {
        OLMatrix4f modelMatrix = new OLMatrix4f();
        modelMatrix.identity();
        modelMatrix.translate(position);
        modelMatrix.rotate((float) Math.toRadians(rotation.x), new OLVector3f(1, 0, 0));
        modelMatrix.rotate((float) Math.toRadians(rotation.y), new OLVector3f(0, 1, 0));
        modelMatrix.rotate((float) Math.toRadians(rotation.z), new OLVector3f(0, 0, 1));
        modelMatrix.scale(scale);
        return modelMatrix;
    }
    
    public OLVector3f getPosition() {
        return position;
    }
    
    public void setPosition(OLVector3f position) {
        this.position = position;
    }
    
    public OLVector3f getVelocity() {
        return velocity;
    }
    
    public void setVelocity(OLVector3f velocity) {
        this.velocity = velocity;
    }
    
    public float getLifeLength() {
        return lifeLength;
    }
    
    public void setLifeLength(float lifeLength) {
        this.lifeLength = lifeLength;
    }
    
    public boolean isInfinity() {
        return infinity;
    }
    
    public void setInfinity(boolean infinity) {
        this.infinity = infinity;
    }
    
    public float getElapsedTime() {
        return elapsedTime;
    }
    
    public OLVector3f getRotation() {
        return rotation;
    }
    
    public void setRotation(OLVector3f rotation) {
        this.rotation = rotation;
    }
    
    public OLVector3f getScale() {
        return scale;
    }
    
    public void setScale(OLVector3f scale) {
        this.scale = scale;
    }
    
    public float getGravityEffect() {
        return gravityEffect;
    }
    
    public void setGravityEffect(float gravityEffect) {
        this.gravityEffect = gravityEffect;
    }
}