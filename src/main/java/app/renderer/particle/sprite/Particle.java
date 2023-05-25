package app.renderer.particle.sprite;

import app.math.OLVector3f;
import app.math.components.OLTransform;

public class Particle {
    private static class ParticleData {
        public OLVector3f position;
        public OLVector3f initPosition;
        public OLVector3f velocity;
        public OLVector3f initVelocity;
        public OLVector3f scale;
        public OLVector3f rotation;
        public float gravityEffect;
        public float lifeLength;
        public float elapsedTime;
        public boolean isInfinity;

        public ParticleData(OLVector3f position, OLVector3f rotation, OLVector3f scale,
                            OLVector3f velocity, float gravityEffect, float lifeLength) {
            this.initPosition = new OLVector3f();
            this.initPosition.x = getRandomNumber(position.x + 3, position.x - 3);
            this.initPosition.y = getRandomNumber(position.y + 3, position.y - 3);
            this.initPosition.z = getRandomNumber(position.z + 3, position.z - 3);
            this.position = new OLVector3f(this.initPosition);
            this.velocity = velocity;
            this.initVelocity = new OLVector3f(velocity);
            this.scale = scale;
            this.rotation = rotation;
            this.gravityEffect = gravityEffect;
            this.lifeLength = lifeLength;
            this.elapsedTime = 0;
            this.isInfinity = false;
        }

        private float getRandomNumber(float min, float max) {
            return (float) ((Math.random() * (max - min)) + min);
        }
    }

    private final ParticleData data;

    public Particle(OLVector3f position, OLVector3f rotation, OLVector3f scale,
                    OLVector3f velocity, float gravityEffect, float lifeLength) {
        this.data = new ParticleData(position, rotation, scale, velocity, gravityEffect, lifeLength);
    }

    public Particle(Particle particle) {
        this.data = new ParticleData(
                particle.data.position, particle.data.rotation, particle.data.scale,
                particle.data.velocity, particle.data.gravityEffect, particle.data.lifeLength
        );
    }


    public OLTransform getTransform() {
        return new OLTransform(data.position, data.scale, data.rotation);
    }

    public boolean isLife() {
        return data.elapsedTime < data.lifeLength;
    }

    public void update(float dt) {
        if (isLife()) {
            //TODO use compute for all Particles
            data.velocity.y -= data.gravityEffect * dt;
            data.position = data.position.add(data.velocity.mul(dt));
            data.elapsedTime += dt;
        } else if (data.isInfinity) {
            data.position = new OLVector3f(data.initPosition);
            data.velocity = new OLVector3f(data.initVelocity);
            data.elapsedTime = 0;
        }
    }

    public void setInfinity(boolean infinity) {
        data.isInfinity = infinity;
    }
}
