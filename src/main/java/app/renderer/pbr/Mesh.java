package app.renderer.pbr;


import app.math.OLVector3f;

import java.io.Serializable;
//TODO: need to add uuid 64 bit long java random number
public record Mesh(float[] vertices, float[] textures, float[] normals, int[] indices,
                   String name, OLVector3f center) implements Serializable {
}

