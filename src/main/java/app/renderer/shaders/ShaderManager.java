package app.renderer.shaders;

import java.util.ArrayList;
import java.util.List;
//TODO Object pooling
public class ShaderManager {
    private static final List<ShaderProgram> shaderPrograms = new ArrayList<>();

    private ShaderManager() {
    }

    public static void add(ShaderProgram program) {
        shaderPrograms.add(program);
    }

    public static void cleanUp() {
        shaderPrograms.forEach(ShaderProgram::cleanUp);
    }
}
