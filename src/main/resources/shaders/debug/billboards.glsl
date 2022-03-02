#type VERTEX
#version 460 core
layout (location = 0) in vec3 position;

uniform mat4 projection;
uniform mat4 view;
uniform vec3 centerPosition;


void main() {
    vec3 CameraRight_worldspace = vec3(ViewMatrix[0][0], ViewMatrix[1][0], ViewMatrix[2][0]);
    vec3 CameraUp_worldspace = vec3(ViewMatrix[0][1], ViewMatrix[1][1], ViewMatrix[2][1]);

    vec3 p = centerPosition + CameraRight_worldspace * position.x + CameraUp_worldspace * position.y;

    gl_Position = view * projection * vec4(p, 1.0); 
}

#type FRAGMENT
#version 460 core
out vec4 FragColor;


void main() {

    FragColor = vec3(1.0);

}