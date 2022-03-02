#type VERTEX
#version 460 core
layout (location = 0) in vec3 position;

uniform mat4 projection;
uniform mat4 view;
uniform vec3 centerPosition;


void main() {
    vec3 CameraRight_worldspace = vec3(view[0][0], view[1][0], view[2][0]);
    vec3 CameraUp_worldspace = vec3(view[0][1], view[1][1], view[2][1]);

    vec3 p = centerPosition + CameraRight_worldspace * position.x + CameraUp_worldspace * position.y;

    gl_Position = view * projection * vec4(p, 1.0); 
}

#type FRAGMENT
#version 460 core
out vec4 FragColor;


void main() {

    FragColor = vec3(1.0);

}