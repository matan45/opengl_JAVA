#type VERTEX
#version 460 core
layout (location = 0) in vec3 position;


layout (std140, binding = 0) uniform Matrices
{
    mat4 projection;
    mat4 view;
};
uniform mat4 model;


void main()
{
    vec3 CameraRight_worldspace = vec3(view[0][0], view[1][0], view[2][0]);
    vec3 CameraUp_worldspace = vec3(view[0][1], view[1][1], view[2][1]);

    vec3 p = centerPosition + CameraRight_worldspace * position.x * 0.5f + CameraUp_worldspace * position.y * -0.5f;

    vec4 positionRelativeToCam = view * vec4(p, 1.0);
    gl_Position = projection * positionRelativeToCam;
}

#type FRAGMENT
#version 460 core
out vec4 FragColor;


void main()
{
    FragColor = vec4(1.0, 1.0, 0.0, 1.0);
}
