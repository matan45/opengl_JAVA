package app.renderer.terrain.quadtree;

import app.math.OLVector3f;
import app.math.components.Camera;

import java.util.LinkedList;
import java.util.List;

public class TerrainQuadtree {

    private static final int VMB_TERRAIN_REC_CUTOFF = 100;
    private static final int MAX_TERRAIN_NODES = 500;

    TerrainNode terrainTree;
    List<TerrainNode> terrainTreeTail;
    int numTerrainNodes = 0;

    Camera camera;

    public TerrainQuadtree(Camera camera) {
        this.camera = camera;
        terrainTree = new TerrainNode();
        terrainTreeTail = new LinkedList<>();
    }

    /**
     * Allocates a new node in the terrain quadtree with the specified parameters.
     */
    public TerrainNode createNode(TerrainNode parent, OLVector3f origin, int type, float width, float height) {
        if (numTerrainNodes >= MAX_TERRAIN_NODES)
            return null;
        numTerrainNodes++;

        TerrainNode terrainNodeTail = new TerrainNode();
        terrainNodeTail.setType(type);
        terrainNodeTail.setOrigin(origin);
        terrainNodeTail.setHeight(height);
        terrainNodeTail.setWidth(width);
        terrainNodeTail.setTscaleNegx(1.0f);
        terrainNodeTail.setTscaleNegz(1.0f);
        terrainNodeTail.setTscalePosx(1.0f);
        terrainNodeTail.setTscalePosz(1.0f);
        terrainNodeTail.setP(parent);
        terrainNodeTail.setN(null);
        terrainNodeTail.setS(null);
        terrainNodeTail.setE(null);
        terrainNodeTail.setW(null);

        terrainTreeTail.add(terrainNodeTail);
        return terrainNodeTail;

    }

    /**
     * Resets the terrain quadtree.
     */
    private void terrain_clearTree() {
        terrainTreeTail.clear();
        numTerrainNodes = 0;
    }

    /**
     * Determines whether a node should be subdivided based on its distance to the camera.
     * Returns true if the node should be subdivided.
     */
    private boolean checkDivide(TerrainNode node) {
        // Distance from current origin to camera
        float d = (float) Math.abs(Math.sqrt(Math.pow(camera.getPosition().x - node.getOrigin().x, 2.0) + Math.pow(camera.getPosition().z - node.getOrigin().z, 2.0)));

        // Check base case:
        // Distance to camera is greater than twice the length of the diagonal
        // from current origin to corner of current square.
        // OR
        // Max recursion level has been hit
        if (d > 2.5 * Math.sqrt(Math.pow(0.5 * node.getWidth(), 2.0) + Math.pow(0.5 * node.getHeight(), 2.0)) || node.getWidth() < VMB_TERRAIN_REC_CUTOFF) {
            return false;
        }

        return true;
    }


}
