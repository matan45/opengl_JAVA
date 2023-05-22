#type VERTEX
#version 460 core
layout (location = 0) in vec2 position;
layout (location = 1) in mat4 model;


layout (std140, binding = 0) uniform Matrices
{
    mat4 projection;
    mat4 view;
};

out vec2 TexCoords;

void main()
{
    vec3 CameraRight_worldspace = vec3(view[0][0], view[1][0], view[2][0]);
    vec3 CameraUp_worldspace = vec3(view[0][1], view[1][1], view[2][1]);

    vec3 p = CameraRight_worldspace * position.x * 0.5f + CameraUp_worldspace * position.y * -0.5f;

    vec4 worldPosition = model * vec4(p, 1.0);
    vec4 positionRelativeToCam = view *  worldPosition;
    gl_Position = projection * positionRelativeToCam;

    TexCoords = position.xy + vec2(0.5f);
}

#type FRAGMENT
#version 460 core
out vec4 FragColor;

in vec2 TexCoords;

uniform sampler2D image;

void main()
{
    vec4 color = texture(image,TexCoords);

    if(color == vec4(0.0))
        discard;

    FragColor = color;
}
