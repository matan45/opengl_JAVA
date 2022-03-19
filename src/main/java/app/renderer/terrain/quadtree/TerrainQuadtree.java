package app.renderer.terrain.quadtree;

import app.math.OLVector3f;
import app.math.components.Camera;

import java.util.ArrayList;
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
        terrainTreeTail = new ArrayList<>(MAX_TERRAIN_NODES);
        terrain_clearTree();
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

    /**
     * Returns true if node is sub-divided. False otherwise.
     */
    private void terrain_divideNode(TerrainNode node) {
        // Subdivide
        float w_new = 0.5f * node.getWidth();
        float h_new = 0.5f * node.getHeight();

        // Create the child nodes
        node.setC1(createNode(node, new OLVector3f(node.getOrigin().x - 0.5f * w_new, node.getOrigin().y, node.getOrigin().z - 0.5f * h_new), 1, w_new, h_new));
        node.setC2(createNode(node, new OLVector3f(node.getOrigin().x + 0.5f * w_new, node.getOrigin().y, node.getOrigin().z - 0.5f * h_new), 2, w_new, h_new));
        node.setC3(createNode(node, new OLVector3f(node.getOrigin().x + 0.5f * w_new, node.getOrigin().y, node.getOrigin().z + 0.5f * h_new), 3, w_new, h_new));
        node.setC4(createNode(node, new OLVector3f(node.getOrigin().x - 0.5f * w_new, node.getOrigin().y, node.getOrigin().z + 0.5f * h_new), 4, w_new, h_new));

        // Assign neighbors
        if (node.getType() == 1) {
            node.setE(node.getP().getC2());
            node.setN(node.getP().getC4());
        } else if (node.getType() == 2) {
            node.setW(node.getP().getC1());
            node.setN(node.getP().getC3());
        } else if (node.getType() == 3) {
            node.setS(node.getP().getC2());
            node.setW(node.getP().getC4());
        } else if (node.getType() == 4) {
            node.setS(node.getP().getC1());
            node.setE(node.getP().getC3());
        }

        // Check if each of these four child nodes will be subdivided.
        boolean div1, div2, div3, div4;
        div1 = checkDivide(node.getC1());
        div2 = checkDivide(node.getC2());
        div3 = checkDivide(node.getC3());
        div4 = checkDivide(node.getC4());

        if (div1)
            terrain_divideNode(node.getC1());
        if (div2)
            terrain_divideNode(node.getC2());
        if (div3)
            terrain_divideNode(node.getC3());
        if (div4)
            terrain_divideNode(node.getC4());
    }

    /**
     * Builds a terrain quadtree based on specified parameters and current camera position.
     */
    void terrain_createTree(OLVector3f origin, float width, float height) {
        terrain_clearTree();

        terrainTree = new TerrainNode();
        terrainTree.setType(0);
        terrainTree.setOrigin(origin);
        terrainTree.setHeight(height);
        terrainTree.setWidth(width);
        terrainTree.setTscaleNegx(1.0f);
        terrainTree.setTscaleNegz(1.0f);
        terrainTree.setTscalePosx(1.0f);
        terrainTree.setTscalePosz(1.0f);
        terrainTree.setP(null);
        terrainTree.setN(null);
        terrainTree.setS(null);
        terrainTree.setE(null);
        terrainTree.setW(null);

        terrainTreeTail.set(0, terrainTree);

        // Recursively subdivide the terrain
        terrain_divideNode(terrainTree);
    }

    /**
     * Search for a node in the tree.
     * x, z == the point we are searching for (trying to find the node with an origin closest to that point)
     * n = the current node we are testing
     */
    TerrainNode find(TerrainNode n, float x, float z) {
        if (n.getOrigin().x == x && n.getOrigin().z == z)
            return n;

        if (n.getC1() == null && n.getC2() == null && n.getC3() == null && n.getC4() == null)
            return n;

        if (n.getOrigin().x >= x && n.getOrigin().z >= z && n.getC1() != null)
            return find(n.getC1(), x, z);
        else if (n.getOrigin().x <= x && n.getOrigin().z >= z && n.getC2() != null)
            return find(n.getC2(), x, z);
        else if (n.getOrigin().x <= x && n.getOrigin().z <= z && n.getC3() != null)
            return find(n.getC3(), x, z);
        else if (n.getOrigin().x >= x && n.getOrigin().z <= z && n.getC4() != null)
            return find(n.getC4(), x, z);

        return n;
    }

    /**
     * Calculate the tessellation scale factor for a node depending on the neighboring patches.
     */
    void calcTessScale(TerrainNode node) {
        TerrainNode t;

        // Positive Z (north)
        t = find(terrainTree, node.getOrigin().x, node.getOrigin().z + 1 + node.getWidth() / 2.0f);
        if (t.getWidth() > node.getWidth())
            node.setTscaleNegz(2.0f);

        // Positive X (east)
        t = find(terrainTree, node.getOrigin().x + 1 + node.getWidth() / 2.0f, node.getOrigin().z);
        if (t.getWidth() > node.getWidth())
            node.setTscalePosx(2.0f);

        // Negative Z (south)
        t = find(terrainTree, node.getOrigin().x, node.getOrigin().z - 1 - node.getWidth() / 2.0f);
        if (t.getWidth() > node.getWidth())
            node.setTscaleNegz(2.0f);

        // Negative X (west)
        t = find(terrainTree, node.getOrigin().x - 1 - node.getWidth() / 2.0f, node.getOrigin().z);
        if (t.getWidth() > node.getWidth())
            node.setTscaleNegx(2.0f);
    }


}
