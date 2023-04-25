package app.editor.component;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StyleMaterial {
    private List<Style> styleList;

    public record Style(float StyleFlag, float r, float g, float b, float a) {
    }

    public List<Style> getStyleList() {
        return styleList;
    }

    public void setStyleList(List<Style> styleList) {
        this.styleList = styleList;
    }


    public String createJsonObject() {
        return new Gson().toJson(
                styleList.stream().map(s -> {
                    Map<String, Float> jsonObject = new HashMap<>();
                    jsonObject.put("StyleFlag", s.StyleFlag);
                    jsonObject.put("Red", s.r);
                    jsonObject.put("Green", s.g);
                    jsonObject.put("Blue", s.b);
                    jsonObject.put("Alpha", s.a);
                    return jsonObject;
                }).toList()
        );
    }

    public void createMaterialObject(Path filePath) throws FileNotFoundException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath.toAbsolutePath().toString()));
        Gson gson = new Gson();
        JsonArray json = gson.fromJson(bufferedReader, JsonArray.class);

    }

}
