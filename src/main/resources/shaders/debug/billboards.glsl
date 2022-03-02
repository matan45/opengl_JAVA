#type VERTEX
#version 460 core
layout (location = 0) in vec3 position;

uniform mat4 projection;
uniform mat4 view;
uniform vec3 centerPosition;


void main()
{
    vec3 p = position;

    gl_Position = vec4(p, 1.0); // using directly the clipped coordinates
}

#type FRAGMENT
#version 460 core
out vec4 FragColor;


void main() {

    FragColor = vec3(1.0);

}