package app.renderer.particle.sprite;

import app.math.OLVector3f;
import app.math.components.OLTransform;

public class Particle {
    private OLVector3f position;
    private final OLVector3f initPosition;
    private OLVector3f velocity;
    private final OLVector3f initVelocity;
    private OLVector3f scale;
    private OLVector3f rotation;
    private float gravityEffect;
    private final float lifeLength;
    private float elapsedTime;
    private boolean isInfinity;

    public Particle(OLVector3f position, OLVector3f rotation, OLVector3f scale, OLVector3f velocity, float gravityEffect, float lifeLength) {
        this.initPosition = new OLVector3f();
        this.initPosition.x = getRandomNumber(position.x + 3, position.x - 3);
        this.initPosition.y = getRandomNumber(position.y + 3, position.y - 3);
        this.initPosition.z = getRandomNumber(position.z + 3, position.z - 3);
        this.position = new OLVector3f(initPosition);
        this.initVelocity = velocity;
        this.velocity = new OLVector3f(initVelocity);
        this.scale = scale;
        this.rotation = rotation;
        this.gravityEffect = gravityEffect;
        this.lifeLength = lifeLength;
        this.elapsedTime = 0;
        this.isInfinity = false;
    }

    public Particle(Particle particle) {
        this(particle.position, particle.rotation, particle.scale, particle.velocity, particle.gravityEffect, particle.lifeLength);
    }


    public OLTransform getTransform() {
        return new OLTransform(position, scale, rotation);
    }

    public boolean isLife() {
        return elapsedTime < lifeLength;
    }

    public void update(float dt) {
        if (isLife()) {
            //TODO use compute for all Particles
            velocity.y -= gravityEffect * dt;
            position = position.add(velocity.mul(dt));
            elapsedTime += dt;
        } else if (isInfinity) {
            position = new OLVector3f(initPosition);
            velocity = new OLVector3f(initVelocity);
            elapsedTime = 0;
        }
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

    public OLVector3f getScale() {
        return scale;
    }

    public void setScale(OLVector3f scale) {
        this.scale = scale;
    }

    public OLVector3f getRotation() {
        return rotation;
    }

    public void setRotation(OLVector3f rotation) {
        this.rotation = rotation;
    }

    public float getGravityEffect() {
        return gravityEffect;
    }

    public void setGravityEffect(float gravityEffect) {
        this.gravityEffect = gravityEffect;
    }

    public float getRandomNumber(float min, float max) {
        return (float) ((Math.random() * (max - min)) + min);
    }

    public void setInfinity(boolean infinity) {
        isInfinity = infinity;
    }
}
