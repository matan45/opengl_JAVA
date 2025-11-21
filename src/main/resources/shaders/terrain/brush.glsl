#type VERTEX
#version 460 core

layout (location = 0) in vec2 position;

uniform vec3 brushPosition;
uniform float brushSize;
uniform sampler2D TexTerrainHeight;
uniform sampler2D TexTerrainModification;
uniform float TerrainHeightOffset;
uniform float TerrainLength;
uniform float TerrainWidth;
uniform vec3 TerrainOrigin;

layout (std140, binding = 0) uniform Matrices
{
    mat4 projection;
    mat4 view;
};

out vec2 vs_texCoord;
out vec3 vs_worldPos;

vec2 calcTerrainTexCoord(vec3 pos)
{
    return vec2(abs(pos.x - TerrainOrigin.x) / TerrainWidth, abs(pos.z - TerrainOrigin.z) / TerrainLength);
}

void main()
{
    vec2 scaledPos = position * brushSize;
    vec3 worldPos = vec3(brushPosition.x + scaledPos.x, brushPosition.y, brushPosition.z + scaledPos.y);

    // Calculate terrain texture coordinates
    vec2 terrainTexCoord = calcTerrainTexCoord(worldPos);

    // Sample heightmap and modification texture to get terrain height
    float baseHeight = texture(TexTerrainHeight, terrainTexCoord).r;
    float modHeight = texture(TexTerrainModification, terrainTexCoord).r;
    float combinedHeight = (baseHeight + modHeight) * TerrainHeightOffset;

    // Apply terrain height to world position
    worldPos.y = combinedHeight + 0.1; // Slight offset to prevent z-fighting

    vs_worldPos = worldPos;
    vs_texCoord = (position + 1.0) * 0.5;

    gl_Position = projection * view * vec4(worldPos, 1.0);
}


#type FRAGMENT
#version 460 core

in vec2 vs_texCoord;
in vec3 vs_worldPos;

out vec4 FragColor;

uniform vec3 brushPosition;
uniform float brushSize;
uniform vec3 brushColor;
uniform float brushAlpha;
uniform float brushFalloff;
uniform int brushShape; // 0 = circle, 1 = square
uniform float time;

float calculateFalloff(float distance, float radius, float falloffPower)
{
    if (distance > radius) return 0.0;
    
    float normalizedDist = distance / radius;
    return pow(1.0 - normalizedDist, falloffPower);
}

float getShapeDistance(vec2 pos, vec2 center, int shape)
{
    if (shape == 0) { // Circle
        return length(pos - center);
    } else { // Square
        vec2 d = abs(pos - center);
        return max(d.x, d.y);
    }
}

void main()
{
    vec2 brushCenter = brushPosition.xz;
    vec2 currentPos = vs_worldPos.xz;
    
    float distance = getShapeDistance(currentPos, brushCenter, brushShape);
    float falloff = calculateFalloff(distance, brushSize, brushFalloff);
    
    if (falloff <= 0.001) {
        discard;
    }
    
    // Create pulsing effect for better visibility
    float pulse = 0.5 + 0.3 * sin(time * 4.0);
    
    // Create ring effect for brush edge
    float ringMask = smoothstep(0.85, 0.95, distance / brushSize);
    float centerMask = smoothstep(0.0, 0.15, distance / brushSize);
    
    float alpha = mix(falloff * 0.3, falloff * 0.8, pulse) * brushAlpha;
    alpha = max(alpha, ringMask * 0.9 * pulse);
    alpha *= centerMask;
    
    // Add some variation based on texture coordinates for visual interest
    float noise = sin(vs_texCoord.x * 20.0) * sin(vs_texCoord.y * 20.0) * 0.1 + 1.0;
    alpha *= noise;
    
    FragColor = vec4(brushColor, alpha);
}