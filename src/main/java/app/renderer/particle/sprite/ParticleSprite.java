package app.renderer.particle.sprite;

import app.math.components.OLTransform;
import app.renderer.particle.sprite.data.ParticleMaterialSprite;

public class ParticleSprite {
    private final ParticleMaterialSprite data;
    private float elapsedTime;

    public ParticleSprite(ParticleMaterialSprite data) {
        this.data = data;
        elapsedTime = 0;
    }

    public OLTransform getTransform() {
        return new OLTransform(data.getParticlePosition().getUpdatePosition(),
                data.getScale(), data.getRotation());
    }

    public boolean isLife() {
        return elapsedTime < data.getLifeLength();
    }

    public void update(float dt) {
        if (isLife()) {
            //TODO use compute for all Particles
            data.getParticleVelocity().getUpdateVelocity().y = data.getGravityEffect() * dt;
            data.getParticlePosition().setUpdatePosition(
                    data.getParticlePosition().getUpdatePosition()
                            .add(data.getParticleVelocity().getUpdateVelocity().mul(dt))
            );

            elapsedTime += dt;
        } else if (data.isInfinity()) {
            data.getParticlePosition().reset();
            data.getParticleVelocity().reset();
            elapsedTime = 0;
        }
    }

    public void setInfinity(boolean infinity) {
        data.setInfinity(infinity);
    }
}