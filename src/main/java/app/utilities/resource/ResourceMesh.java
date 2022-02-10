package app.utilities.resource;

import app.renderer.pbr.Mesh;
import app.utilities.ArrayUtil;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;

import java.nio.IntBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;

public class ResourceMesh {

    protected Mesh[] readMeshFile(Path path) {
        return loadMeshItem(path.toString(), aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices
                | aiProcess_Triangulate | aiProcess_FixInfacingNormals | aiProcess_LimitBoneWeights);
    }

    private Mesh[] loadMeshItem(String fileName, int flags) {
        AIScene aiScene = aiImportFile(fileName, flags);
        if (aiScene == null) {
            System.err.println("Error loading model");
        }

        int numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        Mesh[] meshes = new Mesh[numMeshes];
        for (int i = 0; i < numMeshes; i++) {
            assert aiMeshes != null;
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            Mesh mesh = processMesh(aiMesh);
            meshes[i] = mesh;
        }

        return meshes;
    }

    private Mesh processMesh(AIMesh aiMesh) {
        List<Float> vertices = processVertices(aiMesh);
        List<Float> textures = processNormals(aiMesh);
        List<Float> normals = processTextCoords(aiMesh);
        List<Integer> indices = processIndices(aiMesh);


        return new Mesh(ArrayUtil.listToArray(vertices), ArrayUtil.listToArray(textures), ArrayUtil.listToArray(normals), ArrayUtil.listIntToArray(indices));
    }


    private List<Float> processVertices(AIMesh aiMesh) {
        List<Float> vertices = new ArrayList<>();
        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        while (aiVertices.remaining() > 0) {
            AIVector3D aiVertex = aiVertices.get();
            vertices.add(aiVertex.x());
            vertices.add(aiVertex.y());
            vertices.add(aiVertex.z());
        }
        return vertices;
    }

    private List<Float> processTextCoords(AIMesh aiMesh) {
        List<Float> textures = new ArrayList<>();
        AIVector3D.Buffer textCoords = aiMesh.mTextureCoords(0);
        int numTextCoords = textCoords != null ? textCoords.remaining() : 0;
        for (int i = 0; i < numTextCoords; i++) {
            AIVector3D textCoord = textCoords.get();
            textures.add(textCoord.x());
            textures.add(1 - textCoord.y());
        }
        return textures;
    }

    private List<Float> processNormals(AIMesh aiMesh) {
        List<Float> normals = new ArrayList<>();
        AIVector3D.Buffer aiNormals = aiMesh.mNormals();
        while (aiNormals != null && aiNormals.remaining() > 0) {
            AIVector3D aiNormal = aiNormals.get();
            normals.add(aiNormal.x());
            normals.add(aiNormal.y());
            normals.add(aiNormal.z());
        }
        return normals;
    }

    private List<Integer> processIndices(AIMesh aiMesh) {
        List<Integer> indices = new ArrayList<>();
        int numFaces = aiMesh.mNumFaces();
        AIFace.Buffer aiFaces = aiMesh.mFaces();
        for (int i = 0; i < numFaces; i++) {
            AIFace aiFace = aiFaces.get(i);
            IntBuffer buffer = aiFace.mIndices();
            while (buffer.remaining() > 0) {
                indices.add(buffer.get());
            }
        }
        return indices;
    }
}
