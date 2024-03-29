package app.renderer.terrain;

import app.math.OLVector3f;
import app.math.components.Camera;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL40.GL_PATCHES;

public class TerrainQuadtree {

    private static final int MAX_NODE_SIZE = 512;
    private static final int MAX_TERRAIN_NODES = 500;

    private TerrainNode terrainRootNode;

    private int numTerrainNodes = 0;
    private int renderDepth = 0;

    private final Camera camera;
    private final ShaderTerrainQuadtree shaderTerrainQuadtree;

    public TerrainQuadtree(Camera camera, ShaderTerrainQuadtree shaderTerrainQuadtree) {
        this.camera = camera;
        this.shaderTerrainQuadtree = shaderTerrainQuadtree;
        terrainClearTree();
    }

    /**
     * Allocates a new node in the terrain quadtree with the specified parameters.
     */
    private TerrainNode createNode(TerrainNode parent, float originX, float originY, float originZ, int type, float width, float height) {
        if (numTerrainNodes >= MAX_TERRAIN_NODES)
            return null;
        numTerrainNodes++;

        TerrainNode terrainNodeTail = new TerrainNode();
        terrainNodeTail.type = type;
        terrainNodeTail.originX = originX;
        terrainNodeTail.originY = originY;
        terrainNodeTail.originZ = originZ;
        terrainNodeTail.height = height;
        terrainNodeTail.width = width;
        terrainNodeTail.scaleNegx = 1.0f;
        terrainNodeTail.scaleNegz = 1.0f;
        terrainNodeTail.scalePosx = 1.0f;
        terrainNodeTail.scalePosz = 1.0f;
        terrainNodeTail.p = parent;
        terrainNodeTail.n = null;
        terrainNodeTail.s = null;
        terrainNodeTail.e = null;
        terrainNodeTail.w = null;

        return terrainNodeTail;

    }

    /**
     * Resets the terrain quadtree.
     */
    private void terrainClearTree() {
        numTerrainNodes = 0;
    }

    /**
     * Determines whether a node should be subdivided based on its distance to the camera.
     * Returns true if the node should be subdivided.
     */
    private boolean checkDivide(TerrainNode node) {
        // Distance from current origin to camera
        float d = (float) Math.abs(Math.sqrt(Math.pow(camera.getPosition().x - node.originX, 2.0) + Math.pow(camera.getPosition().z - node.originZ, 2.0)));

        // Check base case:
        // Distance to camera is greater than twice the length of the diagonal
        // from current origin to corner of current square.
        // OR
        // Max recursion level has been hit
        return (d <= 2.5 * Math.sqrt(Math.pow(0.5 * node.width, 2.0) + Math.pow(0.5 * node.height, 2.0))) && (node.width >= MAX_NODE_SIZE);
    }

    /**
     * Returns true if node is sub-divided. False otherwise.
     */
    private void terrainDivideNode(TerrainNode node) {
        // Subdivide
        float widthNew = 0.5f * node.width;
        float heightNew = 0.5f * node.height;

        // Create the child nodes
        node.c1 = createNode(node, node.originX - 0.5f * widthNew, node.originY, node.originZ - 0.5f * heightNew, 1, widthNew, heightNew);
        node.c2 = createNode(node, node.originX + 0.5f * widthNew, node.originY, node.originZ - 0.5f * heightNew, 2, widthNew, heightNew);
        node.c3 = createNode(node, node.originX + 0.5f * widthNew, node.originY, node.originZ + 0.5f * heightNew, 3, widthNew, heightNew);
        node.c4 = createNode(node, node.originX - 0.5f * widthNew, node.originY, node.originZ + 0.5f * heightNew, 4, widthNew, heightNew);

        // Assign neighbors
        if (node.type == 1) {
            node.e = node.p.c2;
            node.n = node.p.c4;
        } else if (node.type == 2) {
            node.w = node.p.c1;
            node.n = node.p.c3;
        } else if (node.type == 3) {
            node.s = node.p.c2;
            node.w = node.p.c4;
        } else if (node.type == 4) {
            node.s = node.p.c1;
            node.e = node.p.c3;
        }

        // Check if each of these four child nodes will be subdivided.
        boolean div1;
        boolean div2;
        boolean div3;
        boolean div4;

        div1 = checkDivide(node.c1);
        div2 = checkDivide(node.c2);
        div3 = checkDivide(node.c3);
        div4 = checkDivide(node.c4);

        if (div1)
            terrainDivideNode(node.c1);
        if (div2)
            terrainDivideNode(node.c2);
        if (div3)
            terrainDivideNode(node.c3);
        if (div4)
            terrainDivideNode(node.c4);
    }

    /**
     * Builds a terrain quadtree based on specified parameters and current camera position.
     */
    public void terrainCreateTree(float originX, float originY, float originZ, float width, float height) {
        terrainClearTree();

        terrainRootNode = new TerrainNode();
        terrainRootNode.type = 0;
        terrainRootNode.originX = originX;
        terrainRootNode.originY = originY;
        terrainRootNode.originZ = originZ;
        terrainRootNode.height = height;
        terrainRootNode.width = width;
        terrainRootNode.scaleNegx = 1.0f;
        terrainRootNode.scaleNegz = 1.0f;
        terrainRootNode.scalePosx = 1.0f;
        terrainRootNode.scalePosz = 1.0f;
        terrainRootNode.p = null;
        terrainRootNode.n = null;
        terrainRootNode.s = null;
        terrainRootNode.e = null;
        terrainRootNode.w = null;

        // Recursively subdivide the terrain
        terrainDivideNode(terrainRootNode);
    }

    /**
     * Search for a node in the tree.
     * x, z == the point we are searching for (trying to find the node with an origin closest to that point)
     * n = the current node we are testing
     */
    private TerrainNode find(TerrainNode n, float x, float z) {
        if (n.originX == x && n.originZ == z)
            return n;

        if (n.c1 == null && n.c2 == null && n.c3 == null && n.c4 == null)
            return n;

        if (n.originX >= x && n.originZ >= z && n.c1 != null)
            return find(n.c1, x, z);
        else if (n.originX <= x && n.originZ >= z && n.c2 != null)
            return find(n.c2, x, z);
        else if (n.originX <= x && n.originZ <= z && n.c3 != null)
            return find(n.c3, x, z);
        else if (n.originX >= x && n.originZ <= z && n.c4 != null)
            return find(n.c4, x, z);

        return n;
    }

    /**
     * Calculate the tessellation scale factor for a node depending on the neighboring patches.
     */
    private void calcTessScale(TerrainNode node) {
        TerrainNode t;

        // Positive Z (north)
        t = find(terrainRootNode, node.originX, node.originZ + 1 + node.width / 2.0f);
        if (t.width > node.width)
            node.scalePosz = 2.0f;

        // Positive X (east)
        t = find(terrainRootNode, node.originX + 1 + node.width / 2.0f, node.originZ);
        if (t.width > node.width)
            node.scalePosx = 2.0f;

        // Negative Z (south)
        t = find(terrainRootNode, node.originX, node.originZ - 1 - node.width / 2.0f);
        if (t.width > node.width)
            node.scaleNegz = 2.0f;

        // Negative X (west)
        t = find(terrainRootNode, node.originX - 1 - node.width / 2.0f, node.originZ);
        if (t.width > node.width)
            node.scaleNegx = 2.0f;
    }


    /**
     * Pushes a node (patch) to the GPU to be drawn.
     * note: height parameter is here but not used. currently, only dealing with square terrains (width is used only)
     */
    private void terrainRenderNode(TerrainNode node) {
        // Calculate the tess scale factor
        calcTessScale(node);

        shaderTerrainQuadtree.loadModelMatrix(node.getOlTransform());

        shaderTerrainQuadtree.loadTileScale(node.width);
        shaderTerrainQuadtree.loadScaleNegx(node.scaleNegx);
        shaderTerrainQuadtree.loadScaleNegz(node.scaleNegz);
        shaderTerrainQuadtree.loadScalePosx(node.scalePosx);
        shaderTerrainQuadtree.loadScalePosz(node.scalePosz);
        shaderTerrainQuadtree.loadNodePosition(new OLVector3f(node.originX, node.originY, node.originZ));

        glDrawElements(GL_PATCHES, 4, GL_UNSIGNED_INT, 0);
    }

    /**
     * Traverses the terrain quadtree to draw nodes with no children.
     */
    private void terrainRenderRecursive(TerrainNode node) {

        // If all children are null, render this node
        if (node.c1 == null && node.c2 == null && node.c3 == null && node.c4 == null) {
            terrainRenderNode(node);
            renderDepth++;
            return;
        }

        // Otherwise, recurse to the children.
        // Note: we're checking if the child exists. Theoretically, with our algorithm,
        // either all the children are null or all the children are not null.
        // There shouldn't be any other cases, but we check here for safety.
        if (node.c1 != null)
            terrainRenderRecursive(node.c1);
        if (node.c1 != null)
            terrainRenderRecursive(node.c2);
        if (node.c1 != null)
            terrainRenderRecursive(node.c3);
        if (node.c4 != null)
            terrainRenderRecursive(node.c4);
    }

    /**
     * Draw the terrain.
     */
    public void terrainRender() {
        renderDepth = 0;

        terrainRenderRecursive(terrainRootNode);
    }

    public int getRenderDepth() {
        return renderDepth;
    }

    public int getNumTerrainNodes() {
        return numTerrainNodes;
    }
}
