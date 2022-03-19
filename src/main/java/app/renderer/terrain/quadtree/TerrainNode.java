package app.renderer.terrain.quadtree;

import app.math.OLMatrix4f;
import app.math.OLVector3f;

public class TerrainNode {
    float originX;
    float originY;
    float originZ;
    float width;
    float height;
    int type; // 1, 2, 3, 4 -- the child # relative to its parent. (0 == root)

    private final OLMatrix4f olTransform = new OLMatrix4f();

    // Tessellation scale
    float tscaleNegx; // negative x edge
    float tscalePosx; // Positive x edge
    float tscaleNegz; // Negative z edge
    float tscalePosz; // Positive z edge

    TerrainNode p; // Parent
    TerrainNode c1; // Children
    TerrainNode c2;
    TerrainNode c3;
    TerrainNode c4;

    TerrainNode n; // Neighbor to north
    TerrainNode s; // Neighbor to south
    TerrainNode e; // Neighbor to east
    TerrainNode w; // Neighbor to west


    public OLMatrix4f getOlTransform() {
        olTransform.translate(new OLVector3f(originX,originY,originZ));
        return olTransform;
    }
}
