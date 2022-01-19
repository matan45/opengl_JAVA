package app.renderer.shaders;

import app.math.OLMatrix4f;
import app.math.OLQuaternion4f;
import app.math.OLVector2f;
import app.math.OLVector3f;
import app.utilities.resource.ResourceManager;
import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.opengl.GL20.*;

public abstract class ShaderProgram {
    int programID;
    Set<Integer> shadersID = new HashSet<>();
    static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(4 * 4);

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

    protected int getUniformLocation(String uniformName) {
        return glGetUniformLocation(programID, uniformName);
    }

    protected void bindAttribute(int attribute, String variableName) {
        glBindAttribLocation(programID, attribute, variableName);
    }

    protected void loadFloat(int location, float value) {
        glUniform1f(location, value);
    }

    protected void loadVector(int location, OLVector3f vector) {
        glUniform3f(location, vector.x, vector.y, vector.z);
    }

    protected void load4DVector(int location, OLQuaternion4f vector) {
        glUniform4f(location, vector.x, vector.y, vector.z, vector.w);
    }

    protected void load2DVector(int location, OLVector2f vector) {
        glUniform2f(location, vector.x, vector.y);
    }

    protected void loadInt(int location, int value) {
        glUniform1i(location, value);
    }

    protected void loadBoolean(int location, boolean value) {
        float toLoad = 0;
        if (value)
            toLoad = 1;
        glUniform1f(location, toLoad);
    }

    protected void loadMatrix(int location, OLMatrix4f matrix) {
        matrix.store(matrixBuffer);
        matrixBuffer.flip();
        glUniformMatrix4fv(location, false, matrixBuffer);
    }

    public void start() {
        glUseProgram(programID);
    }

    public void stop() {
        glUseProgram(0);
    }

    private int loadShader(String file, int type) {
        StringBuilder shaderSource = ResourceManager.readShader(Paths.get(file));

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
