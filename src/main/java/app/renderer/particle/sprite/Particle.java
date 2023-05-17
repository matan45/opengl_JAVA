package app.renderer.particle.sprite;

import app.math.OLVector3f;

public class Particle {
    private OLVector3f position;
    private float gravityEffect;
    private float lifeLength;
    private float elapsedTime = 0;

    public Particle(OLVector3f position, float gravityEffect, float lifeLength) {
        this.position = position;
        this.gravityEffect = gravityEffect;
        this.lifeLength = lifeLength;
    }

    public void update(float dt){
        //need dt

    }

    public boolean isLife(){
        return elapsedTime < lifeLength;
    }

    public OLVector3f getPosition() {
        return position;
    }

    public void setPosition(OLVector3f position) {
        this.position = position;
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
