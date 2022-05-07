package app.renderer.shaders;

import app.math.OLMatrix4f;
import app.math.OLVector2f;
import app.math.OLVector3f;
import app.utilities.logger.LogError;
import app.utilities.resource.ResourceManager;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.lwjgl.opengl.GL20.*;

public abstract class ShaderProgram {
    private final int programID;
    private final Set<Integer> shadersID;
    private static final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(4 * 4);

    protected ShaderProgram(Path path) {
        shadersID = new HashSet<>();
        loadShader(path);

        programID = glCreateProgram();
        for (int id : shadersID)
            glAttachShader(programID, id);
        glLinkProgram(programID);
        glValidateProgram(programID);
        getAllUniformLocations();

        ShaderManager.add(this);
    }

    protected abstract void getAllUniformLocations();


    protected int getUniformLocation(String uniformName) {
        return glGetUniformLocation(programID, uniformName);
    }

    protected void bindAttribute(int attribute, String variableName) {
        glBindAttribLocation(programID, attribute, variableName);
    }

    protected void loadFloat(int location, float value) {
        checkLocation(location);
        glUniform1f(location, value);
    }

    protected void load3DVector(int location, OLVector3f vector) {
        checkLocation(location);
        glUniform3f(location, vector.x, vector.y, vector.z);
    }

    protected void load4DVector(int location, float x, float y, float z, float w) {
        checkLocation(location);
        glUniform4f(location, x, y, z, w);
    }

    protected void load2DVector(int location, OLVector2f vector) {
        checkLocation(location);
        glUniform2f(location, vector.x, vector.y);
    }

    protected void loadInt(int location, int value) {
        checkLocation(location);
        glUniform1i(location, value);
    }

    protected void loadBoolean(int location, boolean value) {
        checkLocation(location);
        float toLoad = 0;
        if (value)
            toLoad = 1;
        glUniform1f(location, toLoad);
    }

    protected void loadMatrix(int location, OLMatrix4f matrix) {
        checkLocation(location);
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

    private void loadShader(Path file) {
        List<ShaderModel> shaderModels = ResourceManager.readShader(file);

        for (ShaderModel tempShader : shaderModels) {
            int shaderID = glCreateShader(tempShader.type().getValue());
            glShaderSource(shaderID, tempShader.shaderSource());
            glCompileShader(shaderID);
            if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
                System.out.println(glGetShaderInfoLog(shaderID, 500));
                System.err.println("Could not compile shader " + tempShader.type());
                System.exit(-1);
            }
            shadersID.add(shaderID);
        }
    }

    private void checkLocation(int location) {
        if (location == -1) {
            LogError.println("cant find");
        }
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
