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
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;

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

    public void bindBlockBuffer(String uniformName, int location) {
        int index = glGetUniformBlockIndex(programID, uniformName);
        if (index == 0xFFFFFFFF)
            LogError.println("cant find Uniform location " + uniformName);
        glUniformBlockBinding(programID, index, location);
    }

    protected int getUniformLocation(String uniformName) {
        int location = glGetUniformLocation(programID, uniformName);
        if (location == 0xFFFFFFFF)
            LogError.println("cant find Uniform location " + uniformName);
        return location;
    }

    protected void loadFloat(int location, float value) {
        glUniform1f(location, value);
    }

    protected void load3DVector(int location, OLVector3f vector) {
        glUniform3f(location, vector.x, vector.y, vector.z);
    }

    protected void load4DVector(int location, float x, float y, float z, float w) {
        glUniform4f(location, x, y, z, w);
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

    private void loadShader(Path file) {
        List<ShaderModel> shaderModels = ResourceManager.readShader(file);

        for (ShaderModel tempShader : shaderModels) {
            int shaderID = glCreateShader(tempShader.type().getValue());
            glShaderSource(shaderID, tempShader.shaderSource());
            glCompileShader(shaderID);
            if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
                System.out.println(glGetShaderInfoLog(shaderID, 512));
                System.err.println("Could not compile shader " + tempShader.type());
                System.exit(-1);
            }
            shadersID.add(shaderID);
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
