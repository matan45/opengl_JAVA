#type VERTEX
#version 460 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 texcoords;

uniform mat4 model;

out vec4 control_position;
out vec2 control_texcoords;
out vec3 control_normal;


void main(){
    control_position = vec4(position, 1.0);
    control_texcoords = texcoords;
    control_normal = normal;
}


#type CONTROL
#version 460 core

// define the number of CPs in the output patch   
layout (vertices = 4) out;

in vec4 control_position[];
in vec2 control_texcoords[];
in vec3 control_normal[];

out vec4 evaluation_position[];
out vec2 evaluation_texcoords[];
out vec3 evaluation_normal[];

uniform float tessellationFactor;

uniform vec2 viewPort;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;

#define tessellatedEdgeSize 32.0f
// Calculate the tessellation factor based on screen space
// dimensions of the edge
float screenSpaceTessFactor(vec4 p0, vec4 p1)
{
	// Calculate edge mid point
	vec4 midPoint = 0.5 * (p0 + p1);
    float radius = distance(p0, p1) / 2.0;

	// View space
	vec4 v0 = model * view * midPoint;
	
    // Project into clip space
	vec4 clip0 = (projection * (v0 - vec4(radius, vec3(0.0))));
	vec4 clip1 = (projection * (v0 + vec4(radius, vec3(0.0))));


	// Get normalized device coordinates
	clip0 /= clip0.w;
	clip1 /= clip1.w;

	// Convert to viewport coordinates
	clip0.xy *= viewPort;
	clip1.xy *= viewPort;
	
	// Return the tessellation factor based on the screen size 
	// given by the distance of the two edge control points in screen space
	// and a reference (min.) tessellation size for the edge set by the application
	return clamp(distance(clip0, clip1) / tessellatedEdgeSize * tessellationFactor, 1.0, 64.0);
}

void main(){
    // Set the control points of the output patch                                               
    evaluation_texcoords[gl_InvocationID] = control_texcoords[gl_InvocationID];                          
    evaluation_normal[gl_InvocationID]   = control_normal[gl_InvocationID];                            
    evaluation_position[gl_InvocationID] = control_position[gl_InvocationID];                          
                                                                                                
    gl_TessLevelOuter[0] = screenSpaceTessFactor(evaluation_position[3], evaluation_position[0]);
	gl_TessLevelOuter[1] = screenSpaceTessFactor(evaluation_position[0], evaluation_position[1]);
	gl_TessLevelOuter[2] = screenSpaceTessFactor(evaluation_position[1], evaluation_position[2]);
	gl_TessLevelOuter[3] = screenSpaceTessFactor(evaluation_position[2], evaluation_position[3]);
	gl_TessLevelInner[0] = mix(gl_TessLevelOuter[0], gl_TessLevelOuter[3], 0.5);
	gl_TessLevelInner[1] = mix(gl_TessLevelOuter[2], gl_TessLevelOuter[1], 0.5);                  
}


#type EVALUATION
#version 460 core

layout(quads, equal_spacing, cw) in; 

in vec4 evaluation_position[];
in vec2 evaluation_texcoords[];
in vec3 evaluation_normal[];

out vec4 position;                                                                        
out vec2 texcoords;                                                                        
out vec3 normal;  

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;

uniform float gDispFactor; 
uniform sampler2D displacementMap;

vec2 interpolate2D(vec2 v0, vec2 v1, vec2 v2, vec2 v3)                                                   
{                                                                                               
    vec2 a = mix(v0, v1, gl_TessCoord.x);
	vec2 b = mix(v3, v2, gl_TessCoord.x);
	return mix(a, b, gl_TessCoord.y);
} 

vec4 interpolate(vec4 v0, vec4 v1, vec4 v2, vec4 v3)
{
	vec4 a = mix(v0, v1, gl_TessCoord.x);
	vec4 b = mix(v3, v2, gl_TessCoord.x);
	return mix(a, b, gl_TessCoord.y);
}
                                                                                                
vec3 interpolate3D(vec3 v0, vec3 v1, vec3 v2, vec3 v3)                                                   
{                                                                                               
    vec3 a = mix(v0, v1, gl_TessCoord.x);
	vec3 b = mix(v3, v2, gl_TessCoord.x);
	return mix(a, b, gl_TessCoord.y);
}

void main(){

    // Interpolate the attributes of the output vertex using the barycentric coordinates        
    texcoords = interpolate2D(evaluation_texcoords[0], evaluation_texcoords[1], evaluation_texcoords[2], evaluation_texcoords[2]);    
    vec3 Normal = interpolate3D(evaluation_normal[0], evaluation_normal[1], evaluation_normal[2], evaluation_normal[2]);            
    normal = normalize(Normal);                                                     
    position = interpolate(evaluation_position[0], evaluation_position[1], evaluation_position[2], evaluation_position[3]);    
                                                                                          
    // Displace the vertex along the normal                                                                            
    float displace = texture(displacementMap, texcoords.xy).x;
    position.y += displace * gDispFactor;
    vec4 worldPosition = model * position;
    vec4 positionRelativeToCam = view * worldPosition;
 	gl_Position = projection * positionRelativeToCam;                                       

}



#type FRAGMENT
#version 460 core

in vec4 position;                                                                        
in vec2 texcoords;                                                                        
in vec3 normal;  

out vec4 FragColor;

uniform sampler2D displacementMap;

void main(){

    FragColor = texture(displacementMap, texcoords);

}