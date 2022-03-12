#type VERTEX
#version 460 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 texcoords;

out vec3 control_position;
out vec2 control_texcoords;
out vec3 control_normal;


void main(){
    control_position = position;
    control_texcoords = texcoords;
    control_normal = normal;

}


#type CONTROL
#version 460 core

// define the number of CPs in the output patch   
layout (vertices = 3) out;

in vec3 control_position[];
in vec2 control_texcoords[];
in vec3 control_normal[];

out vec3 evaluation_position[];
out vec2 evaluation_texcoords[];
out vec3 evaluation_normal[];

uniform vec3 cameraPosition;

float GetTessLevel(float Distance0, float Distance1)                                            
{                                                                                               
    float AvgDistance = (Distance0 + Distance1) / 2.0;                                          
                                                                                                
    if (AvgDistance <= 2.0) {                                                                   
        return 10.0;                                                                            
    }                                                                                           
    else if (AvgDistance <= 5.0) {                                                              
        return 7.0;                                                                             
    }                                                                                           
    else {                                                                                      
        return 3.0;                                                                             
    }                                                                                           
}       

void main(){
    // Set the control points of the output patch                                               
    evaluation_texcoords[gl_InvocationID] = control_texcoords[gl_InvocationID];                          
    evaluation_normal[gl_InvocationID]   = control_normal[gl_InvocationID];                            
    evaluation_position[gl_InvocationID] = control_position[gl_InvocationID];                          
                                                                                                
    // Calculate the distance from the camera to the three control points                       
    float EyeToVertexDistance0 = distance(cameraPosition, evaluation_position[0]);                     
    float EyeToVertexDistance1 = distance(cameraPosition, evaluation_position[1]);                     
    float EyeToVertexDistance2 = distance(cameraPosition, evaluation_position[2]);                     
                                                                                                
    // Calculate the tessellation levels                                                        
    gl_TessLevelOuter[0] = GetTessLevel(EyeToVertexDistance1, EyeToVertexDistance2);            
    gl_TessLevelOuter[1] = GetTessLevel(EyeToVertexDistance2, EyeToVertexDistance0);            
    gl_TessLevelOuter[2] = GetTessLevel(EyeToVertexDistance0, EyeToVertexDistance1);            
    gl_TessLevelInner[0] = gl_TessLevelOuter[2];                      
}


#type EVALUATION
#version 460 core

layout(triangles, equal_spacing, ccw) in; 

in vec3 evaluation_position[];
in vec2 evaluation_texcoords[];
in vec3 evaluation_normal[];

out vec3 position;                                                                        
out vec2 texcoords;                                                                        
out vec3 normal;  

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;

uniform float gDispFactor; 
uniform sampler2D displacementMap;

vec2 interpolate2D(vec2 v0, vec2 v1, vec2 v2)                                                   
{                                                                                               
    return vec2(gl_TessCoord.x) * v0 + vec2(gl_TessCoord.y) * v1 + vec2(gl_TessCoord.z) * v2;   
}                                                                                               
                                                                                                
vec3 interpolate3D(vec3 v0, vec3 v1, vec3 v2)                                                   
{                                                                                               
    return vec3(gl_TessCoord.x) * v0 + vec3(gl_TessCoord.y) * v1 + vec3(gl_TessCoord.z) * v2;   
}

void main(){

    // Interpolate the attributes of the output vertex using the barycentric coordinates        
    texcoords = interpolate2D(evaluation_texcoords[0], evaluation_texcoords[1], evaluation_texcoords[2]);    
    normal = interpolate3D(evaluation_normal[0], evaluation_normal[1], evaluation_normal[2]);            
    normal = normalize(normal);                                                     
    position = interpolate3D(evaluation_position[0], evaluation_position[1], evaluation_position[2]);    

    vec3 Normal = mat3(model) * normal;                                                                                          
    // Displace the vertex along the normal                                                                            
    float displace = texture(displacementMap, texcoords.xy).x;
    position.y += displace * gDispFactor;
    vec4 worldPosition = model * vec4(position, 1.0);
    vec4 positionRelativeToCam = view * worldPosition;
 	gl_Position = projection * positionRelativeToCam;                                       

}



#type FRAGMENT
#version 460 core

in vec3 position;                                                                        
in vec2 texcoords;                                                                        
in vec3 normal;  

out vec4 FragColor;

void main(){

    FragColor = vec4(1.0);

}