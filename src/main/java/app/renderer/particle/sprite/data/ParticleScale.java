package app.renderer.particle.sprite.data;

import app.math.MathUtil;
import app.math.OLVector3f;

public class ParticleScale {

    private OLVector3f initScale;
    private OLVector3f updateScale;
    private OLVector3f maxOffsetScale;
    private OLVector3f minOffsetScale;

    public ParticleScale() {
        initScale = new OLVector3f();
        updateScale = new OLVector3f();
        maxOffsetScale = new OLVector3f();
        minOffsetScale = new OLVector3f();
    }

    public ParticleScale(OLVector3f initScale, OLVector3f maxOffsetScale, OLVector3f minOffsetScale) {
        this.initScale = MathUtil.getRandomNumber(initScale.add(minOffsetScale), initScale.add(maxOffsetScale));
        this.updateScale = new OLVector3f(this.initScale);
        this.maxOffsetScale = maxOffsetScale;
        this.minOffsetScale = minOffsetScale;
    }

    public void reset() {
        updateScale = new OLVector3f(initScale);
    }

    public OLVector3f getInitScale() {
        return initScale;
    }

    public void setInitScale(OLVector3f initScale) {
        this.initScale = initScale;
    }

    public OLVector3f getUpdateScale() {
        return updateScale;
    }

    public void setUpdateScale(OLVector3f updateScale) {
        this.updateScale = updateScale;
    }

    public OLVector3f getMaxOffsetScale() {
        return maxOffsetScale;
    }

    public void setMaxOffsetScale(OLVector3f maxOffsetScale) {
        this.maxOffsetScale = maxOffsetScale;
    }

    public OLVector3f getMinOffsetScale() {
        return minOffsetScale;
    }

    public void setMinOffsetScale(OLVector3f minOffsetScale) {
        this.minOffsetScale = minOffsetScale;
    }
}
