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
uniform float hasDisplacement;

const float PI = 3.14159265359;
const float heightScale = 0.1f;

vec2 ParallaxMapping(vec2 texCoords, vec3 viewDir)
{
    // number of depth layers
    const float minLayers = 8;
    const float maxLayers = 32;
    float numLayers = mix(maxLayers, minLayers, abs(dot(vec3(0.0, 0.0, 1.0), viewDir)));
    // calculate the size of each layer
    float layerDepth = 1.0 / numLayers;
    // depth of current layer
    float currentLayerDepth = 0.0;
    // the amount to shift the texture coordinates per layer (from vector P)
    vec2 P = viewDir.xy / viewDir.z * heightScale;
    vec2 deltaTexCoords = P / numLayers;

    // get initial values
    vec2  currentTexCoords     = texCoords;
    float currentDepthMapValue = texture(displacementMap, currentTexCoords).r;

    while(currentLayerDepth < currentDepthMapValue)
    {
        // shift texture coordinates along direction of P
        currentTexCoords -= deltaTexCoords;
        // get depthmap value at current texture coordinates
        currentDepthMapValue = texture(displacementMap, currentTexCoords).r;
        // get depth of next layer
        currentLayerDepth += layerDepth;
    }

    // get texture coordinates before collision (reverse operations)
    vec2 prevTexCoords = currentTexCoords + deltaTexCoords;

    // get depth after and before collision for linear interpolation
    float afterDepth  = currentDepthMapValue - currentLayerDepth;
    float beforeDepth = texture(displacementMap, prevTexCoords).r - currentLayerDepth + layerDepth;

    // interpolation of texture coordinates
    float weight = afterDepth / (afterDepth - beforeDepth);
    vec2 finalTexCoords = prevTexCoords * weight + currentTexCoords * (1.0 - weight);

    return finalTexCoords;
}

vec3 getNormalFromMap(vec2 texCoords)
{
    vec3 tangentNormal = texture(normalMap, texCoords).xyz * 2.0 - 1.0;

    vec3 Q1  = dFdx(WorldPos);
    vec3 Q2  = dFdy(WorldPos);
    vec2 st1 = dFdx(texCoords);
    vec2 st2 = dFdy(texCoords);

    vec3 N   = normalize(Normal);
    vec3 T  = normalize(Q1*st2.t - Q2*st1.t);
    vec3 B  = -normalize(cross(N, T));
    mat3 TBN = mat3(T, B, N);

    return normalize(TBN * tangentNormal);
}

vec3 fresnelSchlickRoughness(float cosTheta, vec3 F0, float roughness)
{
    return F0 + (max(vec3(1.0 - roughness), F0) - F0) * pow(clamp(1.0 - cosTheta, 0.0, 1.0), 5.0);
}

void main()
{
        // input lighting data
        vec3 V = normalize(cameraPosition - WorldPos);

        vec2 texCoords = TexCoords;
        if(hasDisplacement > 0 ){
            texCoords = ParallaxMapping(TexCoords,  V);
            if(texCoords.x > 1.0 || texCoords.y > 1.0 || texCoords.x < 0.0 || texCoords.y < 0.0)
                    discard;
        }

        // material properties
        vec3 albedo = texture(albedoMap, texCoords).rgb;
        vec3 emissive = texture(emissiveMap, texCoords).rgb;
        float metallic = texture(metallicMap, texCoords).r;
        float roughness = texture(roughnessMap, texCoords).r;
        float ao = texture(aoMap, texCoords).r;

         vec3 N = getNormalFromMap(texCoords);
         vec3 R = reflect(-V, N);

        // calculate reflectance at normal incidence; if dia-electric (like plastic) use F0
        // of 0.04 and if it's a metal, use the albedo color as F0 (metallic workflow)
        vec3 F0 = vec3(0.04);
        F0 = mix(F0, albedo, metallic);

        // ambient lighting (we now use IBL as the ambient term)
        vec3 F = fresnelSchlickRoughness(max(dot(N, V), 0.0), F0, roughness);

        vec3 kS = F;
        vec3 kD = 1.0 - kS;
        kD *= 1.0 - metallic;

        vec3 irradiance = texture(irradianceMap, N).rgb;
        vec3 diffuse      = irradiance * albedo;

        // sample both the pre-filter map and the BRDF lut and combine them together as per the Split-Sum approximation to get the IBL specular part.
        const float MAX_REFLECTION_LOD = 4.0;
        vec3 prefilteredColor = textureLod(prefilterMap, R,  roughness * MAX_REFLECTION_LOD).rgb;
        vec2 brdf  = texture(brdfLUT, vec2(max(dot(N, V), 0.0), roughness)).rg;
        vec3 specular = prefilteredColor * (F * brdf.x + brdf.y);

        vec3 ambient = (kD * diffuse + specular) * ao;

        vec3 color = ambient + emissive;

        // HDR tonemapping
        color = color / (color + vec3(1.0));
        // gamma correct
        color = pow(color, vec3(1.0/2.2));

        FragColor = vec4(color , 1.0);
}