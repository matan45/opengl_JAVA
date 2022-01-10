package app.renderer;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

public class OpenGLObjects {
    List<Integer> vaos = new ArrayList<Integer>();
    List<Integer> vbos = new ArrayList<Integer>();

    public void cleanUp() {
        for (int vao : vaos) {
            glDeleteVertexArrays(vao);
        }
        for (int vbo : vbos) {
            glDeleteBuffers(vbo);
        }

    }
}
