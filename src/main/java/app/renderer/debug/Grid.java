package app.renderer.debug;

import app.math.components.Camera;
import app.renderer.OpenGLObjects;
import app.renderer.VaoModel;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Grid {
    List<Float> vertices;

    ShaderGrid shaderGrid;

    OpenGLObjects openGLObjects;
    Camera camera;
    int vaoModel;

    public Grid(OpenGLObjects openGLObjects, Camera camera) {
        this.openGLObjects = openGLObjects;
        this.camera = camera;
        vertices = new ArrayList<>();
        shaderGrid = new ShaderGrid(Paths.get("src\\main\\resources\\shaders\\debug\\grid.glsl"));
        init();
        vaoModel = openGLObjects.loadToVAO(listToArray(vertices));
    }

    public void init() {

        vertices.add(1f);
        vertices.add(1f);
        vertices.add(0f);

        vertices.add(-1f);
        vertices.add(-1f);
        vertices.add(0f);

        vertices.add(-1f);
        vertices.add(1f);
        vertices.add(0f);

        vertices.add(-1f);
        vertices.add(-1f);
        vertices.add(0f);

        vertices.add(1f);
        vertices.add(1f);
        vertices.add(0f);

        vertices.add(1f);
        vertices.add(-1f);
        vertices.add(0f);
    }

    public void render() {

        shaderGrid.start();
        shaderGrid.loadViewMatrix(camera.getViewMatrix());
        shaderGrid.loadProjectionMatrix(camera.getProjectionMatrix());

        glBindVertexArray(vaoModel);
        glEnableVertexAttribArray(0);

        glDrawArrays(GL_TRIANGLES, 0, 6);

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

        shaderGrid.stop();
    }

    //TODO MOVE IT after mirage
    public static int[] listIntToArray(List<Integer> list) {
        return list.stream().mapToInt((Integer v) -> v).toArray();
    }

    public static float[] listToArray(List<Float> list) {
        int size = list != null ? list.size() : 0;
        float[] floatArr = new float[size];
        for (int i = 0; i < size; i++) {
            floatArr[i] = list.get(i);
        }
        return floatArr;
    }
}
