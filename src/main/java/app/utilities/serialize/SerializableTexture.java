package app.utilities.serialize;

import app.editor.imgui.ContentBrowser;
import app.renderer.texture.Image;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;

public class SerializableTexture {
    public void writeTextureToFile(Image image, Path path) {
        String newPath = path.getParent() + ContentBrowser.FOLDER_SPLITTER + image.fileName() + "." + FileExtension.IMAGE_EXTENSION.getFileName();

        try (FileOutputStream fileOut = new FileOutputStream(newPath)) {
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(image);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
