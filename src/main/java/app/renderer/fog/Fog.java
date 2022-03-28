package app.renderer.fog;

import app.math.OLVector3f;

public class Fog {
    private float sightRange;
    private final OLVector3f fogColor;

    public Fog() {
        fogColor = new OLVector3f(0.5f, 0.5f, 0.5f);
        sightRange = 0.3f;
    }

    public float getSightRange() {
        return sightRange;
    }

    public void setSightRange(float sightRange) {
        this.sightRange = sightRange;
    }

    public OLVector3f getFogColor() {
        return fogColor;
    }

    public void setFogColor(float r, float g, float b) {
        fogColor.x = r;
        fogColor.y = g;
        fogColor.z = b;
    }
}
