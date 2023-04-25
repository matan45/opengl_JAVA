package app.editor.component;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StyleMaterial {
    private List<Style> styleList;

    public StyleMaterial() {
        styleList = new ArrayList<>();
    }

    public static class Style implements Serializable {
        private String styleName;
        private int styleFlag;
        private float r;
        private float g;
        private float b;
        private float a;

        public Style() {
        }

        public Style(String styleName, int styleFlag, float r, float g, float b, float a) {
            this.styleName = styleName;
            this.styleFlag = styleFlag;
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }

        public String getStyleName() {
            return styleName;
        }

        public void setStyleName(String styleName) {
            this.styleName = styleName;
        }

        public int getStyleFlag() {
            return styleFlag;
        }

        public void setStyleFlag(int styleFlag) {
            this.styleFlag = styleFlag;
        }

        public float getR() {
            return r;
        }

        public void setR(float r) {
            this.r = r;
        }

        public float getG() {
            return g;
        }

        public void setG(float g) {
            this.g = g;
        }

        public float getB() {
            return b;
        }

        public void setB(float b) {
            this.b = b;
        }

        public float getA() {
            return a;
        }

        public void setA(float a) {
            this.a = a;
        }
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
                    Map<String, Object> jsonObject = new HashMap<>();
                    jsonObject.put("StyleName", s.styleName);
                    jsonObject.put("StyleFlag", s.styleFlag);
                    jsonObject.put("Red", s.r);
                    jsonObject.put("Green", s.g);
                    jsonObject.put("Blue", s.b);
                    jsonObject.put("Alpha", s.a);
                    return jsonObject;
                }).toList()
        );
    }

    public void createMaterialObject(Path filePath) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(filePath.toAbsolutePath().toString()));
            Gson gson = new Gson();
            JsonArray json = gson.fromJson(bufferedReader, JsonArray.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

}
