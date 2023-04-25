package app.utilities.serialize;

import app.editor.imgui.ContentBrowser;
import app.renderer.pbr.Mesh;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;

class SerializableMesh {

    protected void writeObjectToFile(Mesh mesh, Path path) {
        String newPath = path.getParent() + ContentBrowser.FOLDER_SPLITTER + mesh.name() + "." + FileExtension.MESH_EXTENSION.getFileName();

        try (FileOutputStream fileOut = new FileOutputStream(newPath)) {
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(mesh);
            objectOut.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
