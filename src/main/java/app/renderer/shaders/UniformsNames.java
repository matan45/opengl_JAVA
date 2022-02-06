package app.renderer.shaders;

public enum UniformsNames {
    PROJECTION("projection"),
    VIEW("view");

    private final String uniformsName;

    UniformsNames(String name) {
        uniformsName = name;
    }

    public String getUniformsName() {
        return uniformsName;
    }
}
