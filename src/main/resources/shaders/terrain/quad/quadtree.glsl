#type VERTEX
#version 460 core

layout (location = 0) in vec3 position;

uniform mat4 model;


void main(){

    gl_Position = model * vec4(position,1);
}


#type CONTROL
#version 460 core

layout(vertices = 16) out;

const int AB = 2;
const int BC = 3;
const int CD = 0;
const int DA = 1;


void main()
{

	if(gl_InvocationID == 0)
	{
			gl_TessLevelOuter[AB] = 1;
			gl_TessLevelOuter[BC] = 1;
			gl_TessLevelOuter[CD] = 1;
			gl_TessLevelOuter[DA] = 1;

			gl_TessLevelInner[0] = 1;
			gl_TessLevelInner[1] = 1;
	}

	gl_out[gl_InvocationID].gl_Position = gl_in[gl_InvocationID].gl_Position;
}


#type EVALUATION
#version 460 core

layout(quads, fractional_odd_spacing, cw) in;

void main(){

    float u = gl_TessCoord.x;
    float v = gl_TessCoord.y;

	// world position
	vec4 position =
	((1 - u) * (1 - v) * gl_in[12].gl_Position +
	u * (1 - v) * gl_in[0].gl_Position +
	u * v * gl_in[3].gl_Position +
	(1 - u) * v * gl_in[15].gl_Position);

	gl_Position = position;
}


#type GEOMETRY
#version 460 core

layout(triangles) in;
layout(line_strip, max_vertices = 4) out;

uniform mat4 projection;
uniform mat4 view;

void main() {

	for (int i = 0; i < gl_in.length(); ++i)
	{
		vec4 position = gl_in[i].gl_Position;
		gl_Position = view * projection * position;
		EmitVertex();
	}

	vec4 position = gl_in[0].gl_Position;
	gl_Position = view * projection * position;
    EmitVertex();

	EndPrimitive();
}


#type FRAGMENT
#version 460 core

out vec4 FragColor;

void main(){

    FragColor = vec4(0.01,1,0.01,1.0);

}