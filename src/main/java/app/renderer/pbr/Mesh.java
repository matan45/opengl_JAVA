package app.renderer.pbr;


import app.math.OLVector3f;

import java.io.Serializable;

public record Mesh(float[] vertices, float[] textures, float[] normals, int[] indices,
                   String name, OLVector3f center, OLVector3f min, OLVector3f max,long id) implements Serializable {
}

