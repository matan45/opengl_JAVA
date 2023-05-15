package app.renderer.particle.sprite;

import app.math.OLVector3f;

public class Particle {
    OLVector3f position;
    OLVector3f velocity;
    float rotation;
    float scale;
    float gravityEffect;
    float lifeLength;
    float elapsedTime = 0;

    public Particle(OLVector3f position, OLVector3f velocity, float rotation, float scale, float gravityEffect, float lifeLength) {
        this.position = position;
        this.velocity = velocity;
        this.rotation = rotation;
        this.scale = scale;
        this.gravityEffect = gravityEffect;
        this.lifeLength = lifeLength;
    }

    public boolean update(){
        //need dt
        return elapsedTime < lifeLength;
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

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getGravityEffect() {
        return gravityEffect;
    }

    public void setGravityEffect(float gravityEffect) {
        this.gravityEffect = gravityEffect;
    }

    public float getLifeLength() {
        return lifeLength;
    }

    public void setLifeLength(float lifeLength) {
        this.lifeLength = lifeLength;
    }

    public float getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(float elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
}
