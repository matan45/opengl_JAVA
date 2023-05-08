package app.renderer.shaders;

import app.math.OLMatrix4f;
import app.math.OLVector2f;
import app.math.OLVector3f;
import app.utilities.logger.LogError;
import app.utilities.resource.ResourceManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.shaderc.Shaderc;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;
import static org.lwjgl.opengl.GL41.glShaderBinary;
import static org.lwjgl.opengl.GL46.GL_SPIR_V_BINARY;
import static org.lwjgl.util.shaderc.Shaderc.*;

public abstract class ShaderProgram {
    private final int programID;
    private final Set<Integer> shadersID;
    private static final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(4 * 4);
    private final long options;
    private final long compiler;

    protected ShaderProgram(Path path) {
        shadersID = new HashSet<>();
        options = shaderc_compile_options_initialize();
        shaderc_compile_options_set_source_language(options, shaderc_source_language_glsl);
        shaderc_compile_options_set_target_env(options, shaderc_target_env_opengl, 4);
        shaderc_compile_options_set_warnings_as_errors(options);
        shaderc_compile_options_set_auto_bind_uniforms(options, false);
        shaderc_compile_options_set_optimization_level(options, shaderc_optimization_level_performance);
        compiler = shaderc_compiler_initialize();

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
            int shaderID = glCreateShader(tempShader.type().getOpenglType());

            long result = Shaderc.shaderc_compile_into_spv(compiler,
                    tempShader.shaderSource(),
                    tempShader.type().getShaderCType(),
                    file.toAbsolutePath().toString(),
                    "main",
                    options);

            long status = Shaderc.shaderc_result_get_compilation_status(result);
            handleFailStatus(status, result, tempShader);

            IntBuffer shaders = IntBuffer.allocate(1);
            shaders.put(shaderID);
            shaders.rewind();
            glShaderBinary(shaders, GL_SPIR_V_BINARY, Objects.requireNonNull(Shaderc.shaderc_result_get_bytes(result)));
            Shaderc.shaderc_result_release(result);

            shadersID.add(shaderID);
        }
    }

    private void handleFailStatus(long status, long result, ShaderModel tempShader) {
        if (status != shaderc_compilation_status_success) {
            System.err.println(Shaderc.shaderc_result_get_error_message(result));
            System.err.println("Could not compile shader " + tempShader.type());
            long warnings = Shaderc.shaderc_result_get_num_warnings(result);
            long errors = Shaderc.shaderc_result_get_num_errors(result);
            System.err.println("Number of warnings " + warnings);
            System.err.println("Number of errors " + errors);
            Shaderc.shaderc_compile_options_release(options);
            Shaderc.shaderc_result_release(result);
            Shaderc.shaderc_compiler_release(compiler);
            System.exit(-1);
        }
    }


    public void cleanUp() {
        stop();
        for (int id : shadersID) {
            glDetachShader(programID, id);
            glDeleteShader(id);
        }
        glDeleteProgram(programID);

        Shaderc.shaderc_compile_options_release(options);
        Shaderc.shaderc_compiler_release(compiler);
    }
}
