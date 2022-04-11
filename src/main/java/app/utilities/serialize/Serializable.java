package app.utilities.serialize;

import app.ecs.Entity;
import app.utilities.logger.LogError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.file.Files;

public class Serializable {
    private static File file;
    private static final String PREFAB_EXTENSION = ".prefab";
    private static final SerializableEntity serializableEntity = new SerializableEntity();

    private Serializable() {
    }

    public static void saveEntity(Entity entity, String folderPath) {
        try {
            Gson gson = new Gson();
            file = new File(folderPath, entity.getName() + PREFAB_EXTENSION);

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

                return null;
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

}
