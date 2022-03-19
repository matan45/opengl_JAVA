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
	gl_Position = projection * model * view * gl_Position;

	tes_terrainTexCoord = terrainTexCoord;
	tes_tessLevel = tcs_tessLevel[0];
}


#type GEOMETRY
#version 460 core

layout(triangles) in;

layout(triangle_strip, max_vertices = 4) out;

in vec2 tes_terrainTexCoord[];
in float tes_tessLevel[];

out vec4 gs_wireColor;
noperspective out vec3 gs_edgeDist;
out vec2 gs_terrainTexCoord;

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

void main(void)
{
	vec4 wireColor = wireframeColor();

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

	// Output verts
	for(int i = 0; i < gl_in.length(); ++i)
	{
		gl_Position = gl_in[i].gl_Position;
		gs_terrainTexCoord = tes_terrainTexCoord[i];
		gs_wireColor = wireColor;

		if (i == 0)
			gs_edgeDist = vec3(ha, 0, 0);
		else if (i == 1)
			gs_edgeDist = vec3(0, hb, 0);
		else
			gs_edgeDist = vec3(0, 0, hc);

		EmitVertex();
	}

	// This closes the the triangle
	gl_Position = gl_in[0].gl_Position;
	gs_edgeDist = vec3(ha, 0, 0);
	gs_terrainTexCoord = tes_terrainTexCoord[0];
	gs_wireColor = wireColor;
	EmitVertex();
	
	EndPrimitive();
}


#type FRAGMENT
#version 460 core

in vec4 gs_wireColor;
noperspective in vec3 gs_edgeDist;
in vec2 gs_terrainTexCoord;

out vec4 FragColor;

uniform float ToggleWireframe;
uniform sampler2D TexTerrainHeight;

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
		FragColor = color;

}