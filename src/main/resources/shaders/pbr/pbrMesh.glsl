#type VERTEX
#version 460 core
layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 texcoords;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;


void main()
{
    vec4 worldPosition = model * vec4(position, 1.0);
	vec4 positionRelativeToCam = view * worldPosition;
 	gl_Position = projection * positionRelativeToCam;
}

#type FRAGMENT
#version 460 core
out vec4 FragColor;

void main()
{

    FragColor = vec4(1.0);
}