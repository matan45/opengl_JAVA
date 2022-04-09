package app.utilities.serialize;

import app.ecs.Entity;
import app.utilities.logger.LogInfo;
import com.google.gson.Gson;

import java.io.*;

public class Serializable {
    private static final Gson gson = new Gson();
    private static File file;
    private static final String PREFAB_EXTENSION = ".prefab";

    private Serializable() {
    }

    public static boolean saveEntity(Entity entity, String folderPath) {
        try {
            file = new File(folderPath, entity.getName() + PREFAB_EXTENSION);
            String json = gson.toJson(entity);

            if (!file.exists() && !file.createNewFile()) {
                LogInfo.println("fail to create new file");
                return false;
            }

            try (FileOutputStream writer = new FileOutputStream(file.getAbsolutePath())) {
                writer.write(json.getBytes());
                writer.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Entity loadEntity(String path) {
        file = new File(path);
        if (file.exists()) {
            try (InputStream inputStream = new FileInputStream(file)) {
                String json = readFromInputStream(inputStream);
                return gson.fromJson(json, Entity.class);
            } catch (IOException e) {
                e.printStackTrace();
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
