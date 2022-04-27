package app.renderer.pbr;


import app.math.OLVector3f;

import java.io.Serializable;

public record Mesh(float[] vertices, float[] textures, float[] normals, int[] indices,
                   String name, OLVector3f center) implements Serializable {
}

