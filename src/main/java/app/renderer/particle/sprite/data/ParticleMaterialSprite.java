package app.renderer.particle.sprite.data;

import app.math.OLVector3f;

public class ParticleMaterialSprite {
    private float lifeLength;
    private float particleAmount;
    private float gravityEffect;
    private String texturePath;
    private boolean infinity;
    private OLVector3f rotation;
    private OLVector3f scale;
    private ParticlePositionSprite particlePosition;
    private ParticleVelocitySprite particleVelocity;

    public ParticleMaterialSprite() {
        lifeLength = 0;
        particleAmount = 0;
        gravityEffect = 0;
        texturePath = "";
        infinity = false;
        particlePosition = new ParticlePositionSprite();
        particleVelocity = new ParticleVelocitySprite();
        rotation = new OLVector3f();
        scale=new OLVector3f();
    }

    public ParticleMaterialSprite(float lifeLength, float particleAmount, float gravityEffect, String texturePath, boolean infinity,
                                  ParticlePositionSprite particlePosition, ParticleVelocitySprite particleVelocity, OLVector3f scale
            , OLVector3f rotation) {
        this.lifeLength = lifeLength;
        this.particleAmount = particleAmount;
        this.gravityEffect = gravityEffect;
        this.texturePath = texturePath;
        this.infinity = infinity;
        this.particlePosition = particlePosition;
        this.scale = scale;
        this.particleVelocity = particleVelocity;
        this.rotation = rotation;
    }

    public float getLifeLength() {
        return lifeLength;
    }

    public void setLifeLength(float lifeLength) {
        this.lifeLength = lifeLength;
    }

    public float getParticleAmount() {
        return particleAmount;
    }

    public void setParticleAmount(float particleAmount) {
        this.particleAmount = particleAmount;
    }

    public float getGravityEffect() {
        return gravityEffect;
    }

    public void setGravityEffect(float gravityEffect) {
        this.gravityEffect = gravityEffect;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }

    public boolean isInfinity() {
        return infinity;
    }

    public void setInfinity(boolean infinity) {
        this.infinity = infinity;
    }

    public ParticlePositionSprite getParticlePosition() {
        return particlePosition;
    }

    public void setParticlePosition(ParticlePositionSprite particlePosition) {
        this.particlePosition = particlePosition;
    }

    public OLVector3f getScale() {
        return scale;
    }

    public void setScale(OLVector3f scale) {
        this.scale = scale;
    }

    public ParticleVelocitySprite getParticleVelocity() {
        return particleVelocity;
    }

    public void setParticleVelocity(ParticleVelocitySprite particleVelocity) {
        this.particleVelocity = particleVelocity;
    }

    public OLVector3f getRotation() {
        return rotation;
    }

    public void setRotation(OLVector3f rotation) {
        this.rotation = rotation;
    }
}
