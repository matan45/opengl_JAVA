package app.renderer.shaders;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;
import static org.lwjgl.opengl.GL40.GL_TESS_CONTROL_SHADER;
import static org.lwjgl.opengl.GL40.GL_TESS_EVALUATION_SHADER;
import static org.lwjgl.opengl.GL43.GL_COMPUTE_SHADER;
import static org.lwjgl.util.shaderc.Shaderc.*;

public enum ShaderType {
    VERTEX(GL_VERTEX_SHADER,shaderc_glsl_vertex_shader),
    FRAGMENT(GL_FRAGMENT_SHADER,shaderc_glsl_fragment_shader),
    GEOMETRY(GL_GEOMETRY_SHADER,shaderc_glsl_geometry_shader),
    TESS_CONTROL(GL_TESS_CONTROL_SHADER,shaderc_glsl_tess_control_shader),
    TESS_EVALUATION(GL_TESS_EVALUATION_SHADER,shaderc_glsl_tess_evaluation_shader),
    COMPUTE(GL_COMPUTE_SHADER,shaderc_glsl_compute_shader);

    private final int openglType;
    private final int shaderCType;

    ShaderType(int value,int shaderCType) {

        this.openglType = value;
        this.shaderCType = shaderCType;
    }

    public int getOpenglType() {
        return openglType;
    }

    public int getShaderCType() {
        return shaderCType;
    }
}
