package app.renderer.shaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.opengl.GL20.*;

public abstract class ShaderProgram {
    int programID;
    Set<Integer> shadersID = new HashSet<>();

    protected ShaderProgram(ShaderModel[] shaderModels) {
        for (ShaderModel shaderModel : shaderModels)
            shadersID.add(loadShader(shaderModel.shaderPath(), shaderModel.type().getValue()));

        programID = glCreateProgram();
        for (int id : shadersID)
            glAttachShader(programID, id);
        bindAttributes();
        glLinkProgram(programID);
        glValidateProgram(programID);
        getAllUniformLocations();
    }

    protected abstract void getAllUniformLocations();

    protected abstract void bindAttributes();

    protected void bindAttribute(int attribute, String variableName) {
        glBindAttribLocation(programID, attribute, variableName);
    }

    public void start() {
        glUseProgram(programID);
    }

    public void stop() {
        glUseProgram(0);
    }

    private int loadShader(String file, int type) {
        //TODO extract this function to a resource manager
        StringBuilder shaderSource = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Could not read file");
            e.printStackTrace();
            System.exit(-1);
        }
        //
        int shaderID = glCreateShader(type);
        glShaderSource(shaderID, shaderSource);
        glCompileShader(shaderID);
        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            System.out.println(glGetShaderInfoLog(shaderID, 500));
            System.err.println("Could not compile shader");
            System.exit(-1);
        }
        return shaderID;
    }

    public void cleanUp() {
        stop();
        for (int id : shadersID) {
            glDetachShader(programID, id);
            glDeleteShader(id);
        }
        glDeleteProgram(programID);
    }
}
