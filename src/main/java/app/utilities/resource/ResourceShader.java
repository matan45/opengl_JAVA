package app.utilities.resource;

import app.renderer.shaders.ShaderModel;
import app.renderer.shaders.ShaderType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

class ResourceShader {
    private static final String TYPE = "#type ";

    public List<ShaderModel> readShaderFile(Path path) {
        StringBuilder shaderSource = new StringBuilder();
        List<ShaderModel> shaderModelList = new ArrayList<>();
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
            e.printStackTrace();
            System.exit(-1);
        }
        return shaderModelList;
    }

    private ShaderType getShaderType(String shaderType) {
        return switch (shaderType) {
            case "VERTEX" -> ShaderType.VERTEX;
            case "FRAGMENT" -> ShaderType.FRAGMENT;
            case "COMPUTE" -> ShaderType.COMPUTE;
            case "GEOMETRY" -> ShaderType.GEOMETRY;
            case "CONTROL" -> ShaderType.TESS_CONTROL;
            case "EVALUATION" -> ShaderType.TESS_EVALUATION;
            default -> null;
        };
    }
}
