package app.utilities.resource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class ResourceShader {
    StringBuilder shaderSource = new StringBuilder();

    protected StringBuilder readShaderFile(Path path) {
        shaderSource.setLength(0);

        try (BufferedReader reader = new BufferedReader(new FileReader(path.toAbsolutePath().toString()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Could not read file");
            e.printStackTrace();
            System.exit(-1);
        }
        return shaderSource;
    }
}
