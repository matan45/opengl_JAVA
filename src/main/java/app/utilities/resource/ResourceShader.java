package app.utilities.resource;

import app.renderer.shaders.ShaderModel;
import app.renderer.shaders.ShaderType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ResourceShader {
    StringBuilder shaderSource = new StringBuilder();
    static final String TYPE = "#type ";
    List<ShaderModel> shaderModelList = new ArrayList<>();

    protected List<ShaderModel> readShaderFile(Path path) {
        shaderSource.setLength(0);
        shaderModelList.clear();
        ShaderType type = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toAbsolutePath().toString()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(TYPE)) {
                    String shaderType = line.substring(TYPE.length());
                    ShaderType temp = getShaderType(shaderType.toUpperCase());
                    if (temp == null) {
                        System.err.println("Could not read shader type");
                        System.exit(-1);
                    } else if (type == null) {
                        type = temp;
                    } else if (!temp.name().equals(type.name())) {
                        shaderModelList.add(new ShaderModel(type, shaderSource.toString()));
                        shaderSource.setLength(0);
                        type = temp;
                    }
                    line = reader.readLine();
                }
                shaderSource.append(line).append("\n");
            }
            shaderModelList.add(new ShaderModel(type, shaderSource.toString()));
        } catch (IOException e) {
            System.err.println("Could not read file");
            e.printStackTrace();
            System.exit(-1);
        }
        return shaderModelList;
    }

    private ShaderType getShaderType(String shaderType) {
        switch (shaderType) {
            case "VERTEX":
                return ShaderType.VERTEX;
            case "FRAGMENT":
                return ShaderType.FRAGMENT;
            default:
                return null;
        }
    }
}
