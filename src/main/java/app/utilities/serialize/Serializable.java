package app.utilities.serialize;

import app.ecs.Entity;
import app.editor.component.Scene;
import app.utilities.logger.LogError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.file.Files;

public class Serializable {
    private static File file;
    private static final SerializableEntity serializableEntity = new SerializableEntity();

    private Serializable() {
    }

    public static void saveEntity(Entity entity, String folderPath) {
        try {
            Gson gson = new Gson();
            file = new File(folderPath, entity.getName() + "." + FileExtension.PREFAB_EXTENSION.getFileName());

            if (file.exists())
                Files.delete(file.toPath());


            if (!file.createNewFile()) {
                LogError.println("fail to create new file");
                return;
            }

            String json = gson.toJson(serializableEntity.serializableEntity(entity));

            try (FileOutputStream writer = new FileOutputStream(file.getAbsolutePath())) {
                writer.write(json.getBytes());
                writer.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
            LogError.println("fail to save the entity");
        }
    }

    public static Entity loadEntity(String path) {
        Gson gson = new Gson();
        file = new File(path);
        if (file.exists()) {
            try (InputStream inputStream = new FileInputStream(file)) {
                String file = readFromInputStream(inputStream);
                JsonObject jsonElement = gson.fromJson(file, JsonObject.class);
                Entity entity = serializableEntity.deserializeEntity(jsonElement.getAsJsonObject());
                entity.setName(entity.getName() + " (prefab)");
                entity.setPath(path);
                return entity;
            } catch (IOException e) {
                e.printStackTrace();
                LogError.println("fail to load a file");
                return null;
            }
        }
        return null;
    }

    private static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    public static void saveEmptyScene(String folderPath) {
        try {
            Gson gson = new Gson();
            Scene scene = new Scene();
            file = new File(folderPath, scene.getName() + "." + FileExtension.SCENE_EXTENSION.getFileName());

            if (file.exists())
                Files.delete(file.toPath());


            if (!file.createNewFile()) {
                LogError.println("fail to create new file");
                return;
            }

            JsonObject jsonScene = new JsonObject();
            jsonScene.addProperty("SceneName", scene.getName());
            jsonScene.addProperty("ScenePath", file.getAbsolutePath());

            String json = gson.toJson(jsonScene);

            try (FileOutputStream writer = new FileOutputStream(file.getAbsolutePath())) {
                writer.write(json.getBytes());
                writer.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
            LogError.println("fail to save the entity");
        }
    }

}
