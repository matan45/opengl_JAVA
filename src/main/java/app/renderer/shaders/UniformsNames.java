package app.renderer.shaders;

public enum UniformsNames {
    PROJECTION("projection"),
    VIEW("view"),
    MODEL("model"),
    CAMERA_POSITION("cameraPosition"),
    MATRICES("Matrices");

    private final String uniformsName;

    UniformsNames(String name) {
        uniformsName = name;
    }

    public String getUniformsName() {
        return uniformsName;
    }
}
