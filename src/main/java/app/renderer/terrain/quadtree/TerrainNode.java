package app.renderer.terrain.quadtree;

import app.math.OLVector3f;
import app.math.components.OLTransform;

public class TerrainNode {
    private OLVector3f origin;
    private float width;
    private float height;
    private int type; // 1, 2, 3, 4 -- the child # relative to its parent. (0 == root)

    private final OLTransform olTransform;

    // Tessellation scale
    private float tscaleNegx; // negative x edge
    private float tscalePosx; // Positive x edge
    private float tscaleNegz; // Negative z edge
    private float tscalePosz; // Positive z edge

    private TerrainNode p; // Parent
    private TerrainNode c1; // Children
    private TerrainNode c2;
    private TerrainNode c3;
    private TerrainNode c4;

    private TerrainNode n; // Neighbor to north
    private TerrainNode s; // Neighbor to south
    private TerrainNode e; // Neighbor to east
    private TerrainNode w; // Neighbor to west

    public TerrainNode() {
        origin = new OLVector3f();

        olTransform = new OLTransform();

        p = new TerrainNode();
        c1 = new TerrainNode();
        c2 = new TerrainNode();
        c3 = new TerrainNode();
        c4 = new TerrainNode();

        n = new TerrainNode();
        s = new TerrainNode();
        e = new TerrainNode();
        w = new TerrainNode();
    }

    public OLVector3f getOrigin() {
        return origin;
    }

    public void setOrigin(OLVector3f origin) {
        this.origin = origin;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getTscaleNegx() {
        return tscaleNegx;
    }

    public void setTscaleNegx(float tscaleNegx) {
        this.tscaleNegx = tscaleNegx;
    }

    public float getTscalePosx() {
        return tscalePosx;
    }

    public void setTscalePosx(float tscalePosx) {
        this.tscalePosx = tscalePosx;
    }

    public float getTscaleNegz() {
        return tscaleNegz;
    }

    public void setTscaleNegz(float tscaleNegz) {
        this.tscaleNegz = tscaleNegz;
    }

    public float getTscalePosz() {
        return tscalePosz;
    }

    public void setTscalePosz(float tscalePosz) {
        this.tscalePosz = tscalePosz;
    }

    public TerrainNode getP() {
        return p;
    }

    public void setP(TerrainNode p) {
        this.p = p;
    }

    public TerrainNode getC1() {
        return c1;
    }

    public void setC1(TerrainNode c1) {
        this.c1 = c1;
    }

    public TerrainNode getC2() {
        return c2;
    }

    public void setC2(TerrainNode c2) {
        this.c2 = c2;
    }

    public TerrainNode getC3() {
        return c3;
    }

    public void setC3(TerrainNode c3) {
        this.c3 = c3;
    }

    public TerrainNode getC4() {
        return c4;
    }

    public void setC4(TerrainNode c4) {
        this.c4 = c4;
    }

    public TerrainNode getN() {
        return n;
    }

    public void setN(TerrainNode n) {
        this.n = n;
    }

    public TerrainNode getS() {
        return s;
    }

    public void setS(TerrainNode s) {
        this.s = s;
    }

    public TerrainNode getE() {
        return e;
    }

    public void setE(TerrainNode e) {
        this.e = e;
    }

    public TerrainNode getW() {
        return w;
    }

    public void setW(TerrainNode w) {
        this.w = w;
    }

    public OLTransform getOlTransform() {
        return olTransform;
    }
}
