package app.boot;

import app.editor.GlfwWindow;

public class Run {
    public static void main(String[] args) {
        GlfwWindow glfwWindow = new GlfwWindow(800, 600, "opengl");
        glfwWindow.run();
    }


}
