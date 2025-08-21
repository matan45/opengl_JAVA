package app.boot;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.GL_MAP_READ_BIT;
import static org.lwjgl.opengl.GL30.glMapBufferRange;
import static org.lwjgl.opengl.GL31C.glDrawArraysInstanced;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;

public class ParticleSystem {
    private static final int NUM_PARTICLES = 1000;

    private long window;
    private int computeProgram;
    private int renderProgram;
    private int vao;
    private int vbo;
    private int particleSSBO;
    private int updatedParticleSSBO;
    FloatBuffer particleBuffer = BufferUtils.createFloatBuffer(NUM_PARTICLES * 3);

    public void run() {
        init();
        loop();

        // Clean up resources
        GL.destroy();
        GLFW.glfwTerminate();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 6);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);

        // Create the window
        window = GLFW.glfwCreateWindow(800, 600, "Particle System", MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Set GLFW callbacks

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(window);

        // Initialize GLEW
        createCapabilities();

        // Compile and link shaders
        computeProgram = createComputeShaderProgram();
        renderProgram = createRenderShaderProgram();

        // Create particle buffer
        initParticles();

        // Create vertex array object (VAO)
        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        // Create vertex buffer object (VBO)
        vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, NUM_PARTICLES * 3 * Float.BYTES, GL15.GL_DYNAMIC_DRAW);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(0);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        // Set clear color
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    private int createComputeShaderProgram() {
        String computeShaderSource = """
                #version 430 core
                   
                   layout(local_size_x = 1) in;
                   
                   layout(std430, binding = 0) buffer ParticleBuffer {
                       vec3 particles[];
                   };
                   layout(std430, binding = 1) buffer UpdatedParticleBuffer {
                       vec3 updatedParticles[];
                   };
                   
                   uniform float seed;
                   
                   vec3 getRandomPosition(uint index) {
                       // Generate random numbers using the index and seed
                       float rand1 = fract(sin(dot(vec2(index, seed), vec2(12.9898, 78.233))) * 43758.5453);
                       float rand2 = fract(sin(dot(vec2(index, seed + 1.0), vec2(12.9898, 78.233))) * 43758.5453);
                       float rand3 = fract(sin(dot(vec2(index, seed + 2.0), vec2(12.9898, 78.233))) * 43758.5453);
                   
                       // Map random numbers to the desired range
                       vec3 position = vec3(rand1 * 2.0 - 1.0, rand2 * 2.0 - 1.0, rand3 * 2.0 - 1.0);
                   
                       return position;
                   }
                   
                   void main() {
                       uint index = gl_GlobalInvocationID.x;
                   
                       // Get random position for the particle
                       vec3 position = getRandomPosition(index);
                   
                       // Assign the position to the particle
                       updatedParticles[index] = position;
                   }
                   
                """;

        int computeShader = GL20.glCreateShader(GL43.GL_COMPUTE_SHADER);
        GL20.glShaderSource(computeShader, computeShaderSource);
        GL20.glCompileShader(computeShader);
        if (GL20.glGetShaderi(computeShader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("Compute shader compilation failed:\n" + GL20.glGetShaderInfoLog(computeShader));
        }

        int program = GL20.glCreateProgram();
        GL20.glAttachShader(program, computeShader);
        GL20.glLinkProgram(program);
        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("Shader program linking failed:\n" + GL20.glGetProgramInfoLog(program));
        }

        GL20.glDeleteShader(computeShader);

        return program;
    }

    private int createRenderShaderProgram() {
        String vertexShaderSource = "#version 460 core\n" +
                "layout(location = 0) in vec3 position;\n" +
                "uniform mat4 projection;\n" +
                "uniform mat4 view;\n" +
                "void main() {\n" +
                "    gl_Position = projection * view * vec4(position, 1.0);\n" +
                "    gl_PointSize = 2.0;\n" +
                "}";

        String fragmentShaderSource = "#version 460 core\n" +
                "out vec4 FragColor;\n" +
                "void main() {\n" +
                "    FragColor = vec4(1.0, 0.0, 0.0, 1.0);\n" +
                "}";

        int vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vertexShader, vertexShaderSource);
        GL20.glCompileShader(vertexShader);
        if (GL20.glGetShaderi(vertexShader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("Vertex shader compilation failed:\n" + GL20.glGetShaderInfoLog(vertexShader));
        }

        int fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fragmentShader, fragmentShaderSource);
        GL20.glCompileShader(fragmentShader);
        if (GL20.glGetShaderi(fragmentShader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("Fragment shader compilation failed:\n" + GL20.glGetShaderInfoLog(fragmentShader));
        }

        int program = GL20.glCreateProgram();
        GL20.glAttachShader(program, vertexShader);
        GL20.glAttachShader(program, fragmentShader);
        GL20.glLinkProgram(program);
        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("Shader program linking failed:\n" + GL20.glGetProgramInfoLog(program));
        }

        GL20.glDeleteShader(vertexShader);
        GL20.glDeleteShader(fragmentShader);

        return program;
    }

    public float getRandomNumber(int min, int max) {
        return (float) ((Math.random() * (max - min)) + min);
    }

    private void initParticles() {

        for (int i = 0; i < NUM_PARTICLES; i++) {
            particleBuffer.put(i * 3, getRandomNumber(-10, 10));
            particleBuffer.put(i * 3 + 1, getRandomNumber(-10, 10));
            particleBuffer.put(i * 3 + 2, getRandomNumber(-10, 10));
        }

        particleBuffer.flip();
        particleSSBO = GL15.glGenBuffers();
        GL15.glBindBuffer(GL_SHADER_STORAGE_BUFFER, particleSSBO);
        GL15.glBufferData(GL_SHADER_STORAGE_BUFFER, particleBuffer, GL15.GL_DYNAMIC_DRAW);
        GL30.glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, particleSSBO);
        GL15.glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);

        updatedParticleSSBO = GL15.glGenBuffers();
        GL15.glBindBuffer(GL_SHADER_STORAGE_BUFFER, updatedParticleSSBO);
        GL15.glBufferData(GL_SHADER_STORAGE_BUFFER, particleBuffer, GL15.GL_DYNAMIC_DRAW);
        GL30.glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, updatedParticleSSBO);
        GL15.glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);

    }

    private void updateParticles() {
        GL20.glUseProgram(computeProgram);
        GL43.glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, particleSSBO);
        GL43.glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, updatedParticleSSBO);
        GL15.glBindBuffer(GL43.GL_COPY_WRITE_BUFFER, particleSSBO);
        GL15.glBindBuffer(GL43.GL_COPY_READ_BUFFER, updatedParticleSSBO);
        GL43.glDispatchCompute(NUM_PARTICLES, 1, 1);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, particleBuffer, GL_STATIC_DRAW);
        GL20.glUseProgram(0);
        GL43.glMemoryBarrier(GL43.GL_SHADER_STORAGE_BARRIER_BIT);


        GL15.glBindBuffer(GL_SHADER_STORAGE_BUFFER, updatedParticleSSBO);
        ByteBuffer particleBuffer2 = glMapBuffer(GL_SHADER_STORAGE_BUFFER, GL_WRITE_ONLY);
        GL15.glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);

        byte[] particleData = new byte[NUM_PARTICLES * 3];
        assert particleBuffer2 != null;
        particleBuffer2.get(particleData);
        System.out.println("read data");
        for (int i = 0; i < particleData.length; i++) {
            System.out.println(particleData[i]);
        }

        GL30.glUnmapBuffer(GL_SHADER_STORAGE_BUFFER);
    }

    private void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL20.glUseProgram(renderProgram);

        // Set projection and view matrices
        float aspectRatio = 800.0f / 600.0f;
        Matrix4f projection = new Matrix4f().perspective((float) Math.toRadians(45.0f), aspectRatio, 0.1f, 100.0f);
        Matrix4f view = new Matrix4f().lookAt(new Vector3f(0.0f, 0.0f, 5.0f), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f));
        int projectionLoc = GL20.glGetUniformLocation(renderProgram, "projection");
        int viewLoc = GL20.glGetUniformLocation(renderProgram, "view");
        FloatBuffer projectionBuffer = BufferUtils.createFloatBuffer(16);
        FloatBuffer viewBuffer = BufferUtils.createFloatBuffer(16);
        projection.get(projectionBuffer);
        view.get(viewBuffer);
        GL20.glUniformMatrix4fv(projectionLoc, false, projectionBuffer);
        GL20.glUniformMatrix4fv(viewLoc, false, viewBuffer);


        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, particleBuffer.capacity(), GL_STREAM_DRAW);
        glBufferSubData(GL_ARRAY_BUFFER, 0, particleBuffer);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        GL30.glBindVertexArray(vao);
        GL20.glVertexAttribPointer(updatedParticleSSBO, 3, GL11.GL_FLOAT, false, 0, 0);
        glDrawArraysInstanced(GL11.GL_POINTS, 0, 1, NUM_PARTICLES);
        GL30.glBindVertexArray(0);

        GL20.glUseProgram(0);
    }

    private void loop() {
        createCapabilities(true);
        GLFW.glfwSetFramebufferSizeCallback(window, (window, width, height) -> GL11.glViewport(0, 0, width, height));

        double lastTime = GLFW.glfwGetTime();
        while (!GLFW.glfwWindowShouldClose(window)) {
            double currentTime = GLFW.glfwGetTime();
            float deltaTime = (float) (currentTime - lastTime);
            lastTime = currentTime;

            GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            updateParticles();
            render();

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new ParticleSystem().run();
    }
}