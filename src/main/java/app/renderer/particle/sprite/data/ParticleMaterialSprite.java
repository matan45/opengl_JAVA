package app.renderer.particle.sprite.data;

import app.math.OLVector3f;

public class ParticleMaterialSprite {
    private float lifeLength;
    private float particleAmount;
    private float gravityEffect;
    private String texturePath;
    private boolean infinity;
    private OLVector3f rotation;
    private ParticlePosition particlePosition;
    private ParticleScale particleScale;
    private ParticleVelocity particleVelocity;

    public ParticleMaterialSprite() {
        lifeLength = 0;
        particleAmount = 0;
        gravityEffect = 0;
        texturePath = "";
        infinity = false;
        particlePosition = new ParticlePosition();
        particleScale = new ParticleScale();
        particleVelocity = new ParticleVelocity();
        rotation = new OLVector3f();
    }

    public ParticleMaterialSprite(float lifeLength, float particleAmount, float gravityEffect, String texturePath, boolean infinity,
                                  ParticlePosition particlePosition, ParticleScale particleScale, ParticleVelocity particleVelocity
            , OLVector3f rotation) {
        this.lifeLength = lifeLength;
        this.particleAmount = particleAmount;
        this.gravityEffect = gravityEffect;
        this.texturePath = texturePath;
        this.infinity = infinity;
        this.particlePosition = particlePosition;
        this.particleScale = particleScale;
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

    public ParticlePosition getParticlePosition() {
        return particlePosition;
    }

    public void setParticlePosition(ParticlePosition particlePosition) {
        this.particlePosition = particlePosition;
    }

    public ParticleScale getParticleScale() {
        return particleScale;
    }

    public void setParticleScale(ParticleScale particleScale) {
        this.particleScale = particleScale;
    }

    public ParticleVelocity getParticleVelocity() {
        return particleVelocity;
    }

    public void setParticleVelocity(ParticleVelocity particleVelocity) {
        this.particleVelocity = particleVelocity;
    }

    public OLVector3f getRotation() {
        return rotation;
    }

    public void setRotation(OLVector3f rotation) {
        this.rotation = rotation;
    }
}
