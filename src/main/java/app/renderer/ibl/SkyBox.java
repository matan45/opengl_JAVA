package app.renderer.ibl;

import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.*;

public class SkyBox {
    ShaderCubeMap shaderCubeMap;
    ShaderIrradiance shaderIrradiance;

    public void init() {
        shaderCubeMap = new ShaderCubeMap(Paths.get("src\\main\\resources\\shaders\\skybox\\background.glsl"));
        shaderIrradiance = new ShaderIrradiance(Paths.get("src\\main\\resources\\shaders\\skybox\\cubmap.glsl"));
        // configure global opengl state
        // -----------------------------
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL); // set depth function to less than AND equal for skybox depth trick.

    }
}
