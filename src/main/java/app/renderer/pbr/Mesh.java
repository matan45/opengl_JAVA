package app.renderer.pbr;


import java.io.Serializable;

public record Mesh(float[] vertices, float[] textures, float[] normals, int[] indices,
                   String name) implements Serializable {
}

