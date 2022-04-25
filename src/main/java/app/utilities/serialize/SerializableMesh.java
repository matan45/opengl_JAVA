package app.utilities.serialize;

import app.renderer.pbr.Mesh;

import java.io.*;

class SerializableMesh {

    protected void writeObjectToFile(Mesh mesh, String path) {
        int lastDotIndex = path.lastIndexOf('.');
        String newPath = path.substring(0, lastDotIndex) + "_" + mesh.name() + "." + FileExtension.MESH_EXTENSION.getFileName();

        try (FileOutputStream fileOut = new FileOutputStream(newPath)) {
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(mesh);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
