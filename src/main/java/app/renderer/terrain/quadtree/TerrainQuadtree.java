package app.renderer.terrain.quadtree;

import app.math.OLVector3f;
import app.math.components.Camera;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL40.GL_PATCHES;

public class TerrainQuadtree {

    private static final int VMB_TERRAIN_REC_CUTOFF = 100;
    private static final int MAX_TERRAIN_NODES = 500;

    private TerrainNode terrainTree;
    private final List<TerrainNode> terrainTreeTail;
    private int numTerrainNodes = 0;

    private int maxRenderDepth = 1;
    private int renderDepth = 0;

    private final Camera camera;
    private final ShaderTerrainQuadtree shaderTerrainQuadtree;

    public TerrainQuadtree(Camera camera, ShaderTerrainQuadtree shaderTerrainQuadtree) {
        this.camera = camera;
        this.shaderTerrainQuadtree = shaderTerrainQuadtree;
        terrainTreeTail = new ArrayList<>(MAX_TERRAIN_NODES);
        terrainClearTree();
    }

    /**
     * Allocates a new node in the terrain quadtree with the specified parameters.
     */
    private TerrainNode createNode(TerrainNode parent, OLVector3f origin, int type, float width, float height) {
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
    private void terrainClearTree() {
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
    private void terrainDivideNode(TerrainNode node) {
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
        boolean div1;
        boolean div2;
        boolean div3;
        boolean div4;

        div1 = checkDivide(node.getC1());
        div2 = checkDivide(node.getC2());
        div3 = checkDivide(node.getC3());
        div4 = checkDivide(node.getC4());

        if (div1)
            terrainDivideNode(node.getC1());
        if (div2)
            terrainDivideNode(node.getC2());
        if (div3)
            terrainDivideNode(node.getC3());
        if (div4)
            terrainDivideNode(node.getC4());
    }

    /**
     * Builds a terrain quadtree based on specified parameters and current camera position.
     */
    public void terrainCreateTree(OLVector3f origin, float width, float height) {
        terrainClearTree();

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
        terrainDivideNode(terrainTree);
    }

    /**
     * Search for a node in the tree.
     * x, z == the point we are searching for (trying to find the node with an origin closest to that point)
     * n = the current node we are testing
     */
    private TerrainNode find(TerrainNode n, float x, float z) {
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
    private void calcTessScale(TerrainNode node) {
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


    /**
     * Pushes a node (patch) to the GPU to be drawn.
     * note: height parameter is here but not used. currently only dealing with square terrains (width is used only)
     */
    private void terrainRenderNode(TerrainNode node) {
        // Calculate the tess scale factor
        calcTessScale(node);

        // Setup matrices
        shaderTerrainQuadtree.loadModelMatrix(node.getOlTransform().getModelMatrix());

        shaderTerrainQuadtree.loadtileScale(node.getWidth());
        shaderTerrainQuadtree.loadtscale_negx(node.getTscaleNegx());
        shaderTerrainQuadtree.loadtscale_negz(node.getTscaleNegz());
        shaderTerrainQuadtree.loadtscale_posx(node.getTscalePosx());
        shaderTerrainQuadtree.loadtscale_posz(node.getTscalePosz());

        // Do it
        glDrawElements(GL_PATCHES, 4, GL_UNSIGNED_INT, 0);
    }

    /**
     * Traverses the terrain quadtree to draw nodes with no children.
     */
    private void terrainRenderRecursive(TerrainNode node) {
        //if (renderDepth >= maxRenderDepth)
        //	return;

        // If all children are null, render this node
        if (node.getC1() == null && node.getC2() == null && node.getC3() == null && node.getC4() == null) {
            terrainRenderNode(node);
            renderDepth++;
            return;
        }

        // Otherwise, recruse to the children.
        // Note: we're checking if the child exists. Theoretically, with our algorithm,
        // either all the children are null or all the children are not null.
        // There shouldn't be any other cases, but we check here for safety.
        if (node.getC1() != null)
            terrainRenderRecursive(node.getC1());
        if (node.getC2() != null)
            terrainRenderRecursive(node.getC2());
        if (node.getC3() != null)
            terrainRenderRecursive(node.getC3());
        if (node.getC4() != null)
            terrainRenderRecursive(node.getC4());
    }

    /**
     * Draw the terrrain.
     */
    public void terrainRender() {
        renderDepth = 0;

        terrainRenderRecursive(terrainTree);
    }


}
