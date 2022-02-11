package app.renderer.debug;

import app.math.OLVector3f;
import app.math.components.Camera;
import app.math.components.OLTransform;
import app.renderer.OpenGLObjects;
import app.renderer.VaoModel;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Grid {
    List<Float> vertices;
    List<Integer> indices;
    private static final int SIZE = 10;

    ShaderGrid shaderGrid;

    OpenGLObjects openGLObjects;
    Camera camera;
    VaoModel vaoModel;
    OLTransform olTransform;

    public Grid(OpenGLObjects openGLObjects, Camera camera) {
        this.openGLObjects = openGLObjects;
        this.camera = camera;
        vertices = new ArrayList<>();
        indices = new ArrayList<>();
        shaderGrid = new ShaderGrid(Paths.get("src\\main\\resources\\shaders\\debug\\grid.glsl"));
        init();
        vaoModel = openGLObjects.loadToVAO(listToArray(vertices), listIntToArray(indices));
        olTransform = new OLTransform(new OLVector3f(), new OLVector3f(10, 0, 10), new OLVector3f());
    }

    public void init() {
        for (int j = 0; j <= SIZE; ++j) {
            for (int i = 0; i <= SIZE; ++i) {
                float x = (float) i / (float) SIZE;
                float y = 0;
                float z = (float) j / (float) SIZE;
                vertices.add(x);
                vertices.add(y);
                vertices.add(z);
            }
        }

        for (int j = 0; j < SIZE; ++j) {
            for (int i = 0; i < SIZE; ++i) {

                int row1 = j * (SIZE + 1);
                int row2 = (j + 1) * (SIZE + 1);

                indices.add(row1 + i);
                indices.add(row1 + i + 1);
                indices.add(row1 + i + 1);
                indices.add(row2 + i + 1);

                indices.add(row2 + i + 1);
                indices.add(row2 + i);
                indices.add(row2 + i);
                indices.add(row1 + i);
            }
        }
    }

    public void render() {
        glHint(GL_LINE_SMOOTH_HINT,GL_NICEST);
        glEnable(GL_LINE_SMOOTH);
        shaderGrid.start();
        shaderGrid.loadModelMatrix(olTransform.getModelMatrix());
        shaderGrid.loadViewMatrix(camera.getViewMatrix());
        shaderGrid.loadProjectionMatrix(camera.getProjectionMatrix());

        glBindVertexArray(vaoModel.vaoID());
        glEnableVertexAttribArray(0);

        glDrawElements(GL_LINES, vaoModel.VertexCount(), GL_UNSIGNED_INT, 0);

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
