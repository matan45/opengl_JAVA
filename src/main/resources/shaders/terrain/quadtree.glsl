#type VERTEX
#version 460 core

layout (location = 0) in vec4 position;

uniform mat4 model;


uniform float TerrainLength;
uniform float TerrainWidth;
uniform vec3 TerrainOrigin;
uniform float tileScale;

out vec2 vs_terrainTexCoord;

vec2 calcTerrainTexCoord(vec4 pos)
{
	return vec2(abs(pos.x - TerrainOrigin.x) / TerrainWidth, abs(pos.z - TerrainOrigin.z) / TerrainLength);
}

void main(void)
{
	// Calcuate texture coordantes (u,v) relative to entire terrain
	vec4 p = model * vec4(position.xyz * tileScale, 1.0);
	vs_terrainTexCoord = calcTerrainTexCoord(p);

	// Send vertex position along
	gl_Position = vec4(position.xyz * tileScale, 1.0);
}


#type CONTROL
#version 460 core

layout(vertices = 4) out;

in vec2 vs_terrainTexCoord[];

patch out float gl_TessLevelOuter[4];
patch out float gl_TessLevelInner[2];

out vec2 tcs_terrainTexCoord[];
out float tcs_tessLevel[];

uniform mat4 view;
uniform mat4 model;

uniform sampler2D TexTerrainHeight;
uniform float TerrainHeightOffset;

uniform float tscale_negx;
uniform float tscale_negz;
uniform float tscale_posx;
uniform float tscale_posz;

/**
* Dynamic level of detail using camera distance algorithm.
*/
float dlodCameraDistance(vec4 p0, vec4 p1, vec2 t0, vec2 t1)
{
	vec4 samp = texture(TexTerrainHeight, t0);
	p0.y = samp[0] * TerrainHeightOffset;
	samp = texture(TexTerrainHeight, t1);
	p1.y = samp[0] * TerrainHeightOffset;

	vec4 view0 = model * view * p0;
	vec4 view1 = model * view * p1;

	float MinDepth = 10.0;
	float MaxDepth = 100000.0;

	float d0 = clamp( (abs(p0.z) - MinDepth) / (MaxDepth - MinDepth), 0.0, 1.0);
	float d1 = clamp( (abs(p1.z) - MinDepth) / (MaxDepth - MinDepth), 0.0, 1.0);

	float t = mix(64, 2, (d0 + d1) * 0.5);

	if (t <= 2.0)
	{ 
		return 2.0;
	}
	if (t <= 4.0)
	{ 
		return 4.0;
	}
	if (t <= 8.0)
	{ 
		return 8.0;
	}
	if (t <= 16.0)
	{ 
		return 16.0;
	}
	if (t <= 32.0)
	{ 
		return 32.0;
	}
	
	return 64.0;
}



void main()
{

	// Outer tessellation level
	gl_TessLevelOuter[0] = dlodCameraDistance(gl_in[3].gl_Position, gl_in[0].gl_Position, tcs_terrainTexCoord[3], tcs_terrainTexCoord[0]);
	gl_TessLevelOuter[1] = dlodCameraDistance(gl_in[0].gl_Position, gl_in[1].gl_Position, tcs_terrainTexCoord[0], tcs_terrainTexCoord[1]);
	gl_TessLevelOuter[2] = dlodCameraDistance(gl_in[1].gl_Position, gl_in[2].gl_Position, tcs_terrainTexCoord[1], tcs_terrainTexCoord[2]);
	gl_TessLevelOuter[3] = dlodCameraDistance(gl_in[2].gl_Position, gl_in[3].gl_Position, tcs_terrainTexCoord[2], tcs_terrainTexCoord[3]);
	
	if (tscale_negx == 2.0)
		gl_TessLevelOuter[0] = max(2.0, gl_TessLevelOuter[0] * 0.5);
	if (tscale_negz == 2.0)
		gl_TessLevelOuter[1] = max(2.0, gl_TessLevelOuter[1] * 0.5);
	if (tscale_posx == 2.0)
		gl_TessLevelOuter[2] = max(2.0, gl_TessLevelOuter[2] * 0.5);
	if (tscale_posz == 2.0)
		gl_TessLevelOuter[3] = max(2.0, gl_TessLevelOuter[3] * 0.5);

	// Inner tessellation level
	gl_TessLevelInner[0] = 0.5 * (gl_TessLevelOuter[0] + gl_TessLevelOuter[3]);
	gl_TessLevelInner[1] = 0.5 * (gl_TessLevelOuter[2] + gl_TessLevelOuter[1]);

	// Pass the patch verts along
	gl_out[gl_InvocationID].gl_Position = gl_in[gl_InvocationID].gl_Position;

	// Output heightmap coordinates
	tcs_terrainTexCoord[gl_InvocationID] = vs_terrainTexCoord[gl_InvocationID];

	// Output tessellation level (used for wireframe coloring)
	tcs_tessLevel[gl_InvocationID] = gl_TessLevelOuter[0];
}


#type EVALUATION
#version 460 core

layout(quads, fractional_even_spacing) in;

patch in float gl_TessLevelOuter[4];
patch in float gl_TessLevelInner[2];

in vec2 tcs_terrainTexCoord[];
in float tcs_tessLevel[];

out vec2 tes_terrainTexCoord;
out float tes_tessLevel;

uniform mat4 view;
uniform mat4 model;
uniform mat4 projection;

uniform sampler2D TexTerrainHeight;
uniform float TerrainHeightOffset;


vec4 interpolate(in vec4 v0, in vec4 v1, in vec4 v2, in vec4 v3)
{
	vec4 a = mix(v0, v1, gl_TessCoord.x);
	vec4 b = mix(v3, v2, gl_TessCoord.x);
	return mix(a, b, gl_TessCoord.y);
}

vec2 interpolate2(in vec2 v0, in vec2 v1, in vec2 v2, in vec2 v3)
{
	vec2 a = mix(v0, v1, gl_TessCoord.x);
	vec2 b = mix(v3, v2, gl_TessCoord.x);
	return mix(a, b, gl_TessCoord.y);
}

void main(){

    // Calculate the vertex position using the four original points and interpolate depneding on the tessellation coordinates.	
	gl_Position = interpolate(gl_in[0].gl_Position, gl_in[1].gl_Position, gl_in[2].gl_Position, gl_in[3].gl_Position);

	// Terrain heightmap coords
	vec2 terrainTexCoord = interpolate2(tcs_terrainTexCoord[0], tcs_terrainTexCoord[1], tcs_terrainTexCoord[2], tcs_terrainTexCoord[3]);

	// Sample the heightmap and offset y position of vertex
	vec4 samp = texture(TexTerrainHeight, terrainTexCoord);
	gl_Position.y = samp[0] * TerrainHeightOffset;

	// Project the vertex to clip space and send it along
	vec4 worldPosition = model * gl_Position;
 	gl_Position = projection * view * worldPosition;

	tes_terrainTexCoord = terrainTexCoord;
	tes_tessLevel = tcs_tessLevel[0];
}


#type GEOMETRY
#version 460 core

layout(triangles) in;

layout(triangle_strip, max_vertices = 3) out;

in vec2 tes_terrainTexCoord[];
in float tes_tessLevel[];

out vec4 gs_wireColor;
noperspective out vec3 gs_edgeDist;
out vec2 gs_terrainTexCoord;
out vec3 worldPosition;
out vec3 tangentNormal;

uniform vec2 Viewport;
uniform float ToggleWireframe;

vec4 wireframeColor()
{
	if (tes_tessLevel[0] == 64.0)
		return vec4(0.0, 0.0, 1.0, 1.0);
	else if (tes_tessLevel[0] >= 32.0)
		return vec4(0.0, 1.0, 1.0, 1.0);
	else if (tes_tessLevel[0] >= 16.0)
		return vec4(1.0, 1.0, 0.0, 1.0);
	else if (tes_tessLevel[0] >= 8.0)
		return vec4(1.0, 1.0, 1.0, 1.0);
	else
		return vec4(1.0, 0.0, 0.0, 1.0);
}


vec3 calcTangent()
{	
	vec3 v0 = gl_in[0].gl_Position.xyz;
	vec3 v1 = gl_in[1].gl_Position.xyz;
	vec3 v2 = gl_in[2].gl_Position.xyz;

	// edges of the face/triangle
    vec3 e1 = v1 - v0;
    vec3 e2 = v2 - v0;
	
	vec2 uv0 = tes_terrainTexCoord[0];
	vec2 uv1 = tes_terrainTexCoord[1];
	vec2 uv2 = tes_terrainTexCoord[2];

    vec2 deltaUV1 = uv1 - uv0;
	vec2 deltaUV2 = uv2 - uv0;
	
	float r = 1.0 / (deltaUV1.x * deltaUV2.y - deltaUV1.y * deltaUV2.x);
	
	return normalize((e1 * deltaUV2.y - e2 * deltaUV1.y)*r);
}

void main()
{
	gs_wireColor = wireframeColor();
	tangentNormal = calcTangent();

	// Calculate edge distances for wireframe
	float ha, hb, hc;
	if (ToggleWireframe == 1.0)
	{
		vec2 p0 = vec2(Viewport * (gl_in[0].gl_Position.xy / gl_in[0].gl_Position.w));
		vec2 p1 = vec2(Viewport * (gl_in[1].gl_Position.xy / gl_in[1].gl_Position.w));
		vec2 p2 = vec2(Viewport * (gl_in[2].gl_Position.xy / gl_in[2].gl_Position.w));

		float a = length(p1 - p2);
		float b = length(p2 - p0);
		float c = length(p1 - p0);
		float alpha = acos( (b*b + c*c - a*a) / (2.0*b*c) );
		float beta = acos( (a*a + c*c - b*b) / (2.0*a*c) );
		ha = abs( c * sin( beta ) );
		hb = abs( c * sin( alpha ) );
		hc = abs( b * sin( alpha ) );
	}
	else
	{
		ha = hb = hc = 0.0;
	}

	vec3 position;
	// Output verts
	for(int i = 0; i < gl_in.length(); ++i)
	{
		gl_Position = gl_in[i].gl_Position;
		gs_terrainTexCoord = tes_terrainTexCoord[i];
		position += gl_in[i].gl_Position.xyz;

		if (i == 0)
			gs_edgeDist = vec3(ha, 0, 0);
		else if (i == 1)
			gs_edgeDist = vec3(0, hb, 0);
		else
			gs_edgeDist = vec3(0, 0, hc);

		EmitVertex();
	}
	
	worldPosition = position * 0.5;
	
	EndPrimitive();
}


#type FRAGMENT
#version 460 core

in vec4 gs_wireColor;
noperspective in vec3 gs_edgeDist;

in vec2 gs_terrainTexCoord;
in vec3 worldPosition;
in vec3 tangentNormal;

out vec4 FragColor;

uniform float ToggleWireframe;
uniform sampler2D TexTerrainHeight;
uniform vec3 cameraPosition;

uniform mat4 model;

uniform samplerCube irradianceMap;

uniform float TerrainLength;
uniform float TerrainWidth;

void colorMapping(vec4 high);

vec3 colormix (vec3 a, vec3 b, float h, float m, float n);
vec3 tricolormix (vec3 a, vec3 b, vec3 c,float h, float m, float n);
vec3 biomeColor (float h);
vec3 getFogColor(vec3 albedo);
vec3 getNormal(vec4 high);

const float zfar = 1000;
uniform vec3 fogColor;
uniform float sightRange;
uniform float isFog;
float getFogFactor(float dist)
{
	return -0.0002 / sightRange * ( dist - (zfar) / 10 * sightRange) + 1;
}

void main(){

	vec4 color = texture(TexTerrainHeight, gs_terrainTexCoord);

	// Wireframe junk
	float d = min(gs_edgeDist.x, gs_edgeDist.y);
	d = min(d, gs_edgeDist.z);

	float LineWidth = 0.75;
	float mixVal = smoothstep(LineWidth - 1, LineWidth + 1, d);

	if (ToggleWireframe == 1.0)
		FragColor = mix(gs_wireColor, color, mixVal);
	else
		colorMapping(color);

}

void colorMapping(vec4 high){
	vec3 albedo = pow(biomeColor(high.r), vec3(2.2));

	// HDR tonemapping
    vec3 color = albedo / (albedo + vec3(1.0));
    // gamma correct
    color = pow(color, vec3(1.0/2.2));

	vec3 normal = getNormal(high);

	vec3 irradiance = texture(irradianceMap, normal).rgb;
    vec3 diffuse    = irradiance * color;

	if(isFog == 1.0){
		FragColor = vec4(getFogColor(diffuse), 1.0);
	} else
		FragColor = vec4(diffuse, 1.0);
}

vec3 getFogColor(vec3 albedo){
	vec3 localPosition = vec4(model * vec4(worldPosition, 1.0)).xyz;
	float dist = length(cameraPosition -  localPosition);
	float fogFactor = getFogFactor(dist);
	return mix(fogColor, albedo, clamp(fogFactor, 0, 1));
}

const vec2 size = vec2(2.0, 0.0);
const vec3 off = vec3(-1.0, 0.0, 1.0);
vec3 getNormal(vec4 high)
{ 
    vec2 offxy = vec2(off.x/TerrainWidth , off.y/TerrainLength);
    vec2 offzy = vec2(off.z/TerrainWidth , off.y/TerrainLength);
    vec2 offyx = vec2(off.y/TerrainWidth , off.x/TerrainLength);
    vec2 offyz = vec2(off.y/TerrainWidth , off.z/TerrainLength);

    float s01 = texture(TexTerrainHeight, gs_terrainTexCoord + offxy).x;
	float s21 = texture(TexTerrainHeight, gs_terrainTexCoord + offzy).x;
	float s10 = texture(TexTerrainHeight, gs_terrainTexCoord + offyx).x;
	float s12 = texture(TexTerrainHeight, gs_terrainTexCoord + offyz).x;
    
    vec3 va = vec3(size.x, size.y, s21-s01);
    vec3 vb = vec3(size.y, size.x, s12-s10);
    va = normalize(va);
    vb = normalize(vb);
	vec3 bump = vec3((cross(va,vb)) / 2 + 0.5);

	/*vec3 bitangent = normalize(cross(tangentNormal, bump));
	mat3 TBN = mat3(tangentNormal, bump, bitangent);

    return normalize(TBN * bump); */

	return bump;
}

//return a color from a to b when h goes from m to n (and divide the color by 255)
vec3 colormix (vec3 a, vec3 b, float h, float m, float n) {
    return mix(a/255.0, b/255.0, (h-m)/(n-m));
}
               
//return a color from a to b to c when h goes from m to n (and divide the color by 255)
vec3 tricolormix (vec3 a, vec3 b, vec3 c,float h, float m, float n) {
	float t = (h-m)/(n-m);
    if (t<0.5) {
    	return mix(a/255.0, b/255.0,t*2.0);
    }
    else {
    	return mix(b/255.0, c/255.0,(t-0.5)*2.0);
    }
}

vec3 biomeColor (float h) {

    float oceanh = 0.2;
    vec3 ocean1 = vec3(8.0 ,42.0 ,79.0);
    vec3 ocean2 = vec3(23.0 ,79.0 ,114.0);
    float seah = 0.32;
    vec3 sea1 = vec3(6.0 ,104.0 ,133.0);
    vec3 sea2 = vec3(56.0 ,104.0 ,133.0);
    float bayh = 0.4;
    vec3 bay1 = vec3(79.0 ,176.0 ,159.0);
    vec3 bay2 = vec3(93.0 ,204.0 ,167.0);
    float shoreh = 0.45;
    vec3 shore1 = vec3(131.0 ,246.0 ,191);
    vec3 shore2 = vec3(234.0 ,246.0 ,191);
    float beachh = 0.5;
    vec3 beach1 = vec3(210.0 ,173.0 ,128.0);
    vec3 beach2 = vec3(255.0 ,236.0 ,181.0);
    float fieldh = 0.74;
    vec3 field1 = vec3(31.0 ,122.0 ,4.0);
    vec3 field2 = vec3(140.0 ,191.0 ,28.0);
    float dirth = 0.92;
    vec3 dirt1 = vec3(154.0 ,148.0 ,9.0);
    vec3 dirt2 = vec3(204.0 ,170.0 ,31.0);
    float rockh = 0.97;
    vec3 rock1 = vec3(133.0 ,140.0 ,112.0);
    vec3 rock2 = vec3(72.0 ,114.0 ,104.0);
    vec3 snow1 = vec3(197.0 ,219.0 ,211.0);
    vec3 snow2 = vec3(224.0 ,255.0 ,255.0);

    if (h<oceanh)    	return colormix(ocean1, ocean2,h,0.0,oceanh);
    if (h<seah)     	return tricolormix(ocean2,sea1,sea2,h,oceanh,seah);
    if (h<bayh)         return tricolormix(sea2, bay1, bay2,h,seah,bayh);
    if (h<shoreh)       return tricolormix(bay2, shore1, shore2,h,bayh,shoreh);
    if (h<beachh)       return colormix(beach1, beach2,h,shoreh,beachh);
    if (h<fieldh)       return colormix(field1,field2,h,beachh,fieldh);
    if (h<dirth)        return tricolormix(field2, dirt1, dirt2,h,fieldh,dirth);
    if (h<rockh)        return tricolormix(dirt2, rock1, rock2,h,dirth,rockh);
	else/*snow*/		return tricolormix(rock2,snow1, snow2,h,rockh,1.0);	
}

