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
    vec4 worldPosition = model * vec4(position, 1.0);
    vec4 positionRelativeToCam = view * worldPosition;
    gl_Position = projection * positionRelativeToCam;
    gl_PointSize = 2.0;
}

#type FRAGMENT
#version 460 core
out vec4 FragColor;


void main()
{
    FragColor = vec4(1.0, 1.0, 0.0, 1.0);
}
