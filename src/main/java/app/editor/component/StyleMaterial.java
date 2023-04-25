package app.editor.component;

import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public void staveToFile(Path filePath) {
        try (FileWriter myWriter = new FileWriter(filePath.toAbsolutePath().toString())) {
            myWriter.write(createJsonObject());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createMaterialObject(Path filePath) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath.toAbsolutePath().toString()));
            Style[] styles = new Gson().fromJson(bufferedReader, Style[].class);
            styleList = Arrays.stream(styles).toList();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String createJsonObject() {
        return new Gson().toJson(styleList.stream().toList());
    }

}
