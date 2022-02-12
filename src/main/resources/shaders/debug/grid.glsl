#type VERTEX
#version 460 core
layout (location = 0) in vec3 position;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;

out vec3 worldPos;

void main()
{
    vec4 worldPosition = model * vec4(position, 1.0);
    worldPos=vec3(worldPosition);
	vec4 positionRelativeToCam = view * worldPosition;
 	gl_Position = projection * positionRelativeToCam;
}

#type FRAGMENT
#version 460 core
out vec4 FragColor;

in vec3 worldPos;

void main()
{
    vec2 coord = worldPos.xz / 10; //square size in world space
    vec2 frac = fract(coord);
    vec2 mult = smoothstep(0.0, 0.5, frac) - smoothstep(1.0-0.5, 1.0, frac);
    vec3 col1=vec3(1.0,0.0,0.0);
    vec3 col2=vec3(0.0,1.0,0.0);
    vec3 col = mix(col1, col2, mult.x * mult.y);
    FragColor = vec4(col,1.0);
}