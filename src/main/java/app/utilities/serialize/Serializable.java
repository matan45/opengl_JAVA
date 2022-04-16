package app.utilities.serialize;

import app.ecs.Entity;
import app.ecs.EntitySystem;
import app.editor.component.Scene;
import app.editor.component.SceneHandler;
import app.utilities.logger.LogError;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class Serializable {
    private static final SerializableEntity serializableEntity = new SerializableEntity();

    private static final String FAIL = "fail to save ";
    private static final String SCENE_NAME = "SceneName";
    private static final String SCENE_PATH = "ScenePath";

    private Serializable() {
    }

    public static void saveEntity(Entity entity, String folderPath) {
        try {
            Gson gson = new Gson();
            File file = new File(folderPath, entity.getName() + "." + FileExtension.PREFAB_EXTENSION.getFileName());

            if (file.exists())
                Files.delete(file.toPath());


            if (!file.createNewFile()) {
                LogError.println("fail to create new " + FileExtension.PREFAB_EXTENSION.getFileName());
                return;
            }
            String json = gson.toJson(serializableEntity.serializableEntity(entity));

            try (FileOutputStream writer = new FileOutputStream(file.getAbsolutePath())) {
                writer.write(json.getBytes());
                writer.flush();
            }

        } catch (IOException e) {
            LogError.println(FAIL + FileExtension.PREFAB_EXTENSION.getFileName());
        }
    }

    public static void saveEntity(Entity entity) {
        try {
            Gson gson = new Gson();
            File file = new File(entity.getPath());

            if (file.exists())
                Files.delete(file.toPath());


            if (!file.createNewFile()) {
                LogError.println("fail to create new " + FileExtension.PREFAB_EXTENSION.getFileName());
                return;
            }
            String json = gson.toJson(serializableEntity.serializableEntity(entity));

            try (FileOutputStream writer = new FileOutputStream(file.getAbsolutePath())) {
                writer.write(json.getBytes());
                writer.flush();
            }

        } catch (IOException e) {
            LogError.println(FAIL + FileExtension.PREFAB_EXTENSION.getFileName());
        }
    }

    public static Entity loadEntity(String path) {
        Gson gson = new Gson();
        File file = new File(path);
        if (file.exists()) {
            try (InputStream inputStream = new FileInputStream(file)) {
                String fileData = readFromInputStream(inputStream);
                JsonObject jsonElement = gson.fromJson(fileData, JsonObject.class);
                Entity entity = serializableEntity.deserializeEntity(jsonElement.getAsJsonObject());
                if (!entity.getName().contains(" (prefab)"))
                    entity.setName(entity.getName() + " (prefab)");
                entity.setPath(path);
                return entity;
            } catch (IOException e) {
                LogError.println("fail to load " + FileExtension.PREFAB_EXTENSION.getFileName());
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
            File file = new File(folderPath, scene.getName() + "." + FileExtension.SCENE_EXTENSION.getFileName());

            if (file.exists())
                Files.delete(file.toPath());


            if (!file.createNewFile()) {
                LogError.println("fail to create " + FileExtension.SCENE_EXTENSION.getFileName());
                return;
            }

            JsonObject jsonScene = new JsonObject();
            jsonScene.addProperty(SCENE_NAME, scene.getName());
            jsonScene.addProperty(SCENE_PATH, scene.getPath().toAbsolutePath().toString());

            String json = gson.toJson(jsonScene);

            try (FileOutputStream writer = new FileOutputStream(file.getAbsolutePath())) {
                writer.write(json.getBytes());
                writer.flush();
            }

        } catch (IOException e) {
            LogError.println(FAIL + FileExtension.SCENE_EXTENSION.getFileName());
        }
    }

    public static void saveScene(String path) {
        try {
            Gson gson = new Gson();
            File file = new File(path);

            if (file.exists())
                Files.delete(file.toPath());


            if (!file.createNewFile()) {
                LogError.println("fail to create " + FileExtension.SCENE_EXTENSION.getFileName());
                return;
            }

            JsonObject jsonScene = new JsonObject();
            jsonScene.addProperty(SCENE_NAME, SceneHandler.getActiveScene().getName());
            jsonScene.addProperty(SCENE_PATH, SceneHandler.getActiveScene().getPath().toAbsolutePath().toString());
            List<Entity> entities = EntitySystem.getEntitiesFather();

            JsonArray jsonEntities = new JsonArray();
            for (Entity entity : entities) {
                jsonEntities.add(serializableEntity.serializableEntity(entity));
            }

            jsonScene.add("entities", jsonEntities);

            String json = gson.toJson(jsonScene);

            try (FileOutputStream writer = new FileOutputStream(file.getAbsolutePath())) {
                writer.write(json.getBytes());
                writer.flush();
            }

        } catch (IOException e) {
            LogError.println(FAIL + FileExtension.SCENE_EXTENSION.getFileName());
        }
    }

    public static void loadScene(String path) {
        File file = new File(path);
        if (file.exists()) {
            Gson gson = new Gson();
            Scene scene = new Scene();
            try (InputStream inputStream = new FileInputStream(file)) {
                String fileData = readFromInputStream(inputStream);
                JsonObject jsonElement = gson.fromJson(fileData, JsonObject.class);
                scene.setName(jsonElement.get(SCENE_NAME).getAsString());
                scene.setPath(Paths.get(jsonElement.get(SCENE_PATH).getAsString()));
                SceneHandler.setActiveScene(scene);

                EntitySystem.closeEntities();
                JsonArray entities = jsonElement.getAsJsonArray("entities");
                if (entities != null) {
                    for (int i = 0; i < entities.size(); i++) {
                        Entity entity = serializableEntity.deserializeEntity(entities.get(i).getAsJsonObject());
                        EntitySystem.addEntity(entity);
                    }
                }

            } catch (IOException e) {
                LogError.println(FAIL + FileExtension.SCENE_EXTENSION.getFileName());
            }
        }
    }

}
