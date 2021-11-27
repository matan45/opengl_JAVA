package app.editor;

import app.editor.imgui.ImguiHandler;
import app.editor.imgui.MainImgui;
import app.editor.imgui.SceneGraph;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL.setCapabilities;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.system.MemoryUtil.NULL;


public class GlfwWindow {
    long window;
    float deltaTime = 0;
    ImguiHandler imgui;
    final String title;
    public static final int WIDTH=1920;
    public static final int HEIGHT =1080;

    public GlfwWindow( String title) {
        this.title = title;
        init();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new RuntimeException("Unable to initialize GLFW");
        //opengl settings
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        //window options
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, GLFW_TRUE);
        //for imgui main window
        glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);

        window = glfwCreateWindow(WIDTH, HEIGHT, title, NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create GLFW window");

        //make opengl context
        glfwMakeContextCurrent(window);

        //Enable v-sync
        glfwSwapInterval(1);

        glfwShowWindow(window);


    }

    private void close() {
        imgui.disposeImGui();

        setCapabilities(null);
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    public void run() {
        //create opengl context
        createCapabilities();

        imgui = new ImguiHandler("#version 460", window);
        //TODO: more generic to add imgui window
        imgui.addLayer(new MainImgui(title));
        imgui.addLayer(new SceneGraph());

        float dt = System.nanoTime();

        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();
            glClear(GL_COLOR_BUFFER_BIT);

            //calculate delta time
            float frame = System.nanoTime();
            deltaTime = ((frame - dt) / 1000000000.0f);
            dt = frame;

            imgui.startFrame();
            imgui.renderImGui();
            imgui.endFrame();

            glfwSwapBuffers(window);
        }
        close();
    }
}
