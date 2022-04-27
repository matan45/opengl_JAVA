package app.utilities.resource;

import app.math.OLVector3f;
import app.renderer.pbr.Mesh;
import app.utilities.ArrayUtil;
import app.utilities.logger.LogError;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;

class ResourceMesh {

    private static final int FLAGS = aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices | aiProcess_OptimizeMeshes
            | aiProcess_Triangulate | aiProcess_FixInfacingNormals
            | aiProcessPreset_TargetRealtime_MaxQuality;

    protected Mesh readMeshFile(Path path) {
        return loadMeshItem(path.toAbsolutePath().toString());
    }

    protected Mesh[] importMeshesFile(Path path) {
        return loadMeshesItem(path.toAbsolutePath().toString());
    }


    private Mesh loadMeshItem(String fileName) {
        try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            return (Mesh) objectInputStream.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private Mesh[] loadMeshesItem(String fileName) {
        AIScene aiScene = aiImportFile(fileName, FLAGS);
        if (aiScene == null) {
            LogError.println("Error loading model");
            return new Mesh[0];
        }

        int numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        Mesh[] meshes = new Mesh[numMeshes];
        for (int i = 0; i < numMeshes; i++) {
            assert aiMeshes != null;
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            Mesh mesh = processMesh(aiMesh, i);
            meshes[i] = mesh;
        }

        return meshes;
    }

    private Mesh processMesh(AIMesh aiMesh, int index) {
        List<Float> vertices = new ArrayList<>();
        List<Float> textures = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        OLVector3f center = new OLVector3f();
        processVertices(aiMesh, vertices, center);
        processNormals(aiMesh, normals);
        processTextCoords(aiMesh, textures);
        processIndices(aiMesh, indices);

        return new Mesh(ArrayUtil.listToArray(vertices), ArrayUtil.listToArray(textures), ArrayUtil.listToArray(normals), ArrayUtil.listIntToArray(indices), aiMesh.mName().dataString() + "_" + index, center);
    }


    private void processVertices(AIMesh aiMesh, List<Float> vertices, OLVector3f center) {
        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        int sumX = 0;
        int sumY = 0;
        int sumZ = 0;
        while (aiVertices.remaining() > 0) {
            AIVector3D aiVertex = aiVertices.get();
            vertices.add(aiVertex.x());
            sumX += aiVertex.x();
            vertices.add(aiVertex.y());
            sumY += aiVertex.y();
            vertices.add(aiVertex.z());
            sumZ += aiVertex.z();
        }
        center.x = sumX / ((float) vertices.size() / 3);
        center.y = sumY / ((float) vertices.size() / 3);
        center.z = sumZ / ((float) vertices.size() / 3);
    }

    private void processTextCoords(AIMesh aiMesh, List<Float> textures) {
        AIVector3D.Buffer textCoords = aiMesh.mTextureCoords(0);
        int numTextCoords = textCoords != null ? textCoords.remaining() : 0;
        for (int i = 0; i < numTextCoords; i++) {
            AIVector3D textCoord = textCoords.get();
            textures.add(textCoord.x());
            textures.add(1 - textCoord.y());
        }
    }

    private void processNormals(AIMesh aiMesh, List<Float> normals) {
        AIVector3D.Buffer aiNormals = aiMesh.mNormals();
        while (aiNormals != null && aiNormals.remaining() > 0) {
            AIVector3D aiNormal = aiNormals.get();
            normals.add(aiNormal.x());
            normals.add(aiNormal.y());
            normals.add(aiNormal.z());
        }
    }

    private void processIndices(AIMesh aiMesh, List<Integer> indices) {
        int numFaces = aiMesh.mNumFaces();
        AIFace.Buffer aiFaces = aiMesh.mFaces();
        for (int i = 0; i < numFaces; i++) {
            AIFace aiFace = aiFaces.get(i);
            IntBuffer buffer = aiFace.mIndices();
            while (buffer.remaining() > 0) {
                indices.add(buffer.get());
            }
        }
    }
}
