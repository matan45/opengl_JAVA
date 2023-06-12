package app.renderer.particle.sprite.data;

import app.math.MathUtil;
import app.math.OLVector3f;

public class ParticleVelocitySprite {

    private OLVector3f initVelocity;
    private OLVector3f updateVelocity;
    private OLVector3f maxOffsetVelocity;
    private OLVector3f minOffsetVelocity;

    public ParticleVelocitySprite() {
        initVelocity = new OLVector3f();
        updateVelocity = new OLVector3f();
        maxOffsetVelocity = new OLVector3f();
        minOffsetVelocity = new OLVector3f();
    }

    public ParticleVelocitySprite(OLVector3f initVelocity, OLVector3f updateVelocity, OLVector3f maxOffsetVelocity, OLVector3f minOffsetVelocity) {
        this.initVelocity = MathUtil.getRandomNumber(initVelocity.add(minOffsetVelocity), initVelocity.add(maxOffsetVelocity));
        this.updateVelocity = new OLVector3f(this.initVelocity);
        this.maxOffsetVelocity = maxOffsetVelocity;
        this.minOffsetVelocity = minOffsetVelocity;
    }

    public void reset() {
        updateVelocity = new OLVector3f(initVelocity);
    }

    public OLVector3f getInitVelocity() {
        return initVelocity;
    }

    public void setInitVelocity(OLVector3f initVelocity) {
        this.initVelocity = initVelocity;
    }

    public OLVector3f getUpdateVelocity() {
        return updateVelocity;
    }

    public void setUpdateVelocity(OLVector3f updateVelocity) {
        this.updateVelocity = updateVelocity;
    }

    public OLVector3f getMaxOffsetVelocity() {
        return maxOffsetVelocity;
    }

    public void setMaxOffsetVelocity(OLVector3f maxOffsetVelocity) {
        this.maxOffsetVelocity = maxOffsetVelocity;
    }

    public OLVector3f getMinOffsetVelocity() {
        return minOffsetVelocity;
    }

    public void setMinOffsetVelocity(OLVector3f minOffsetVelocity) {
        this.minOffsetVelocity = minOffsetVelocity;
    }
}
