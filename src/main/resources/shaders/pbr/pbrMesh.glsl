#type VERTEX
#version 460 core
layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 texcoords;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;

out vec2 TexCoords;
out vec3 WorldPos;
out vec3 Normal;

void main()
{
    TexCoords = texcoords;
    Normal = mat3(model) * normal;
    vec4 worldPosition = model * vec4(position, 1.0);
    WorldPos = worldPosition.xyz;
	vec4 positionRelativeToCam = view * worldPosition;
 	gl_Position = projection * positionRelativeToCam;
}

#type FRAGMENT
#version 460 core
out vec4 FragColor;

in vec2 TexCoords;
in vec3 WorldPos;
in vec3 Normal;

// material parameters
uniform sampler2D albedoMap;
uniform sampler2D normalMap;
uniform sampler2D metallicMap;
uniform sampler2D roughnessMap;
uniform sampler2D aoMap;
uniform sampler2D displacementMap;
uniform sampler2D emissiveMap;

// IBL
uniform samplerCube irradianceMap;
uniform samplerCube prefilterMap;
uniform sampler2D brdfLUT;

uniform vec3 cameraPosition;

const float PI = 3.14159265359;

void main()
{

    FragColor = vec4(1.0);
}