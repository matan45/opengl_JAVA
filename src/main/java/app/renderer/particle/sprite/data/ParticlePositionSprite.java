package app.renderer.particle.sprite.data;

import app.math.MathUtil;
import app.math.OLVector3f;

public class ParticlePositionSprite {
    private OLVector3f initPosition;
    private OLVector3f updatePosition;
    private OLVector3f maxOffsetPosition;
    private OLVector3f minOffsetPosition;

    public ParticlePositionSprite() {
        initPosition = new OLVector3f();
        updatePosition = new OLVector3f();
        maxOffsetPosition = new OLVector3f();
        minOffsetPosition = new OLVector3f();
    }

    public ParticlePositionSprite(OLVector3f initPosition, OLVector3f maxOffsetPosition, OLVector3f minOffsetPosition) {
        this.initPosition = MathUtil.getRandomNumber(initPosition.add(minOffsetPosition), initPosition.add(maxOffsetPosition));
        this.updatePosition = new OLVector3f(this.initPosition);
        this.maxOffsetPosition = maxOffsetPosition;
        this.minOffsetPosition = minOffsetPosition;
    }

    public void reset() {
        updatePosition = new OLVector3f(initPosition);
    }

    public OLVector3f getInitPosition() {
        return initPosition;
    }

    public OLVector3f getUpdatePosition() {
        return updatePosition;
    }

    public OLVector3f getMaxOffsetPosition() {
        return maxOffsetPosition;
    }

    public OLVector3f getMinOffsetPosition() {
        return minOffsetPosition;
    }

    public void setInitPosition(OLVector3f initPosition) {
        this.initPosition = initPosition;
    }

    public void setUpdatePosition(OLVector3f updatePosition) {
        this.updatePosition = updatePosition;
    }

    public void setMaxOffsetPosition(OLVector3f maxOffsetPosition) {
        this.maxOffsetPosition = maxOffsetPosition;
    }

    public void setMinOffsetPosition(OLVector3f minOffsetPosition) {
        this.minOffsetPosition = minOffsetPosition;
    }
}
