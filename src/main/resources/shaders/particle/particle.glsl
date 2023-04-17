#type VERTEX
#version 460 core
layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 texcoords;

layout (std140, binding = 0) uniform Matrices
{
    mat4 projection;
    mat4 view;
};
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
uniform sampler2D emissiveMap;

// IBL
uniform samplerCube irradianceMap;
uniform samplerCube prefilterMap;
uniform sampler2D brdfLUT;

uniform vec3 cameraPosition;

struct DirLight {
    vec3 direction;
	vec3 color;
};

#define MAX_LIGHTS 32

struct PointLight {
    vec3 position;
	vec3 color;

	float constant;
	float linear;
	float quadratic;
};

struct SpotLight {
    vec3 position;
    vec3 direction;
	vec3 color;

	float cutOff;
	float outerCutOff;

	float constant;
	float linear;
	float quadratic;
};

uniform DirLight dirLight;
uniform float dirLightIntensity;

uniform int pointLightSize;
uniform PointLight pointLight[MAX_LIGHTS];

uniform int spotLightSize;
uniform SpotLight spotLight[MAX_LIGHTS];

vec3 CalcDirLight(vec3 N, vec3 V, vec3 albedo, vec3 F0, float metallic, float roughness);
vec3 CalcPointLight(vec3 N, vec3 V, vec3 albedo, vec3 F0, float metallic, float roughness);
vec3 CalcSpotLight(vec3 N, vec3 V, vec3 albedo, vec3 F0, float metallic, float roughness);

const float zfar = 1000;
uniform vec3 fogColor;
uniform float sightRange;
uniform float isFog;
float getFogFactor(float dist)
{
	return -0.0002 / sightRange * ( dist - (zfar) / 10 * sightRange) + 1;
}

const float PI = 3.14159265359;

// ----------------------------------------------------------------------------
float DistributionGGX(vec3 N, vec3 H, float roughness)
{
    float a = roughness*roughness;
    float a2 = a*a;
    float NdotH = max(dot(N, H), 0.0);
    float NdotH2 = NdotH*NdotH;

    float nom   = a2;
    float denom = (NdotH2 * (a2 - 1.0) + 1.0);
    denom = PI * denom * denom;

    return nom / denom;
}
// ----------------------------------------------------------------------------
float GeometrySchlickGGX(float NdotV, float roughness)
{
    float r = (roughness + 1.0);
    float k = (r*r) / 8.0;

    float nom   = NdotV;
    float denom = NdotV * (1.0 - k) + k;

    return nom / denom;
}
// ----------------------------------------------------------------------------
float GeometrySmith(vec3 N, vec3 V, vec3 L, float roughness)
{
    float NdotV = max(dot(N, V), 0.0);
    float NdotL = max(dot(N, L), 0.0);
    float ggx2 = GeometrySchlickGGX(NdotV, roughness);
    float ggx1 = GeometrySchlickGGX(NdotL, roughness);

    return ggx1 * ggx2;
}
// ----------------------------------------------------------------------------
vec3 fresnelSchlick(float cosTheta, vec3 F0)
{
    return F0 + (1.0 - F0) * pow(clamp(1.0 - cosTheta, 0.0, 1.0), 5.0);
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

        // material properties
        vec3 albedo =  pow(texture(albedoMap, TexCoords).rgb, vec3(2.2));
        vec3 emission = texture(emissiveMap, TexCoords).rgb;
        float metallic = texture(metallicMap, TexCoords).r;
        float roughness = texture(roughnessMap, TexCoords).r;
        float ao = texture(aoMap, TexCoords).r;

         vec3 N = getNormalFromMap(TexCoords);
         vec3 R = reflect(-V, N);

        // calculate reflectance at normal incidence; if dia-electric (like plastic) use F0
        // of 0.04 and if it's a metal, use the albedo color as F0 (metallic workflow)
        vec3 F0 = vec3(0.04);
        F0 = mix(F0, albedo, metallic);

        vec3 DirLight = vec3(0.0);
        DirLight = CalcDirLight(N, V, albedo, F0, metallic, roughness);

        vec3 PointLight = vec3(0.0);
        PointLight += CalcPointLight(N, V, albedo, F0, metallic, roughness);

        vec3 SpotLight = vec3(0.0);
        SpotLight += CalcSpotLight(N, V, albedo, F0, metallic, roughness);

        // ambient lighting (we now use IBL as the ambient term)
        vec3 F = fresnelSchlickRoughness(max(dot(N, V), 0.0), F0, roughness);

        vec3 kS = F;
        vec3 kD = 1.0 - kS;
        kD *= 1.0 - metallic;

        vec3 irradiance = texture(irradianceMap, N).rgb;
        vec3 diffuse    = (irradiance * albedo) + emission;

        // sample both the pre-filter map and the BRDF lut and combine them together as per the Split-Sum approximation to get the IBL specular part.
        const float MAX_REFLECTION_LOD = 4.0;
        vec3 prefilteredColor = textureLod(prefilterMap, R,  roughness * MAX_REFLECTION_LOD).rgb;
        vec2 brdf  = texture(brdfLUT, vec2(max(dot(N, V), 0.0), roughness)).rg;
        vec3 specular = prefilteredColor * (F * brdf.x + brdf.y);

        vec3 ambient = (kD * diffuse + specular) * ao;

        vec3 color = ambient + DirLight + PointLight + SpotLight;

        // HDR tonemapping
        color = color / (color + vec3(1.0));
        // gamma correct
        color = pow(color, vec3(1.0/2.2));

        //fog
        if(isFog == 1.0){
            float dist = length(cameraPosition - WorldPos);
	        float fogFactor = getFogFactor(dist);
	        color = mix(fogColor, color, clamp(fogFactor, 0, 1));
        }

        FragColor = vec4(color , 1.0);
}


vec3 CalcDirLight(vec3 N, vec3 V, vec3 albedo, vec3 F0, float metallic, float roughness){
        // calculate per-light radiance
        vec3 L = normalize(-dirLight.direction);
        vec3 H = normalize(V + L);
        vec3 radiance = dirLight.color;

        // Cook-Torrance BRDF
        float NDF = DistributionGGX(N, H, roughness);
        float G   = GeometrySmith(N, V, L, roughness);
        vec3 F    = fresnelSchlick(max(dot(H, V), 0.0), F0);

        vec3 numerator    = NDF * G * F;
        float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.0001; // + 0.0001 to prevent divide by zero
        vec3 specular = numerator / denominator;

         // kS is equal to Fresnel
        vec3 kS = F;
        // for energy conservation, the diffuse and specular light can't
        // be above 1.0 (unless the surface emits light); to preserve this
        // relationship the diffuse component (kD) should equal 1.0 - kS.
        vec3 kD = vec3(1.0) - kS;
        // multiply kD by the inverse metalness such that only non-metals
        // have diffuse lighting, or a linear blend if partly metal (pure metals
        // have no diffuse light).
        kD *= 1.0 - metallic;

        // scale light by NdotL
        float NdotL = max(dot(N, L), 0.0);

        // add to outgoing radiance Lo
       // note that we already multiplied the BRDF by the Fresnel (kS) so we won't multiply by kS again
        return  (kD * albedo / PI + specular) * radiance * dirLightIntensity * NdotL; ;
}

vec3 CalcPointLight(vec3 N, vec3 V, vec3 albedo, vec3 F0, float metallic, float roughness){

        vec3 Lo = vec3(0.0);
        for(int i = 0; i < pointLightSize; i++) {
            // calculate per-light radiance
            vec3 L = normalize(pointLight[i].position - WorldPos);
            vec3 H = normalize(V + L);
            float distance = length(pointLight[i].position - WorldPos);
            float attenuation = 1.0 / (pointLight[i].constant + pointLight[i].linear * distance + pointLight[i].quadratic * (distance * distance));
            vec3 radiance = pointLight[i].color * attenuation;

             // Cook-Torrance BRDF
            float NDF = DistributionGGX(N, H, roughness);
            float G   = GeometrySmith(N, V, L, roughness);
            vec3 F    = fresnelSchlick(max(dot(H, V), 0.0), F0);

            vec3 numerator    = NDF * G * F;
            float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.0001; // + 0.0001 to prevent divide by zero
            vec3 specular = numerator / denominator;

             // kS is equal to Fresnel
             vec3 kS = F;
            // for energy conservation, the diffuse and specular light can't
            // be above 1.0 (unless the surface emits light); to preserve this
            // relationship the diffuse component (kD) should equal 1.0 - kS.
            vec3 kD = vec3(1.0) - kS;
            // multiply kD by the inverse metalness such that only non-metals
            // have diffuse lighting, or a linear blend if partly metal (pure metals
            // have no diffuse light).
            kD *= 1.0 - metallic;

            // scale light by NdotL
            float NdotL = max(dot(N, L), 0.0);

            Lo += (kD * albedo / PI + specular) * radiance * NdotL;

        }
        return  Lo;
}


vec3 CalcSpotLight(vec3 N, vec3 V, vec3 albedo, vec3 F0, float metallic, float roughness){

        vec3 Lo = vec3(0.0);
        for(int i = 0; i < spotLightSize; i++) {
            // calculate per-light radiance
            vec3 L = normalize(spotLight[i].position - WorldPos);
            vec3 H = normalize(V + L);
            float distance = length(spotLight[i].position - WorldPos);
            float attenuation = 1.0 / (spotLight[i].constant + spotLight[i].linear * distance + spotLight[i].quadratic * (distance * distance));
            vec3 radiance = spotLight[i].color * attenuation;

            float theta = dot(L, normalize(-spotLight[i].direction));
            float epsilon = spotLight[i].cutOff - spotLight[i].outerCutOff;
            float intensity = clamp((theta - spotLight[i].outerCutOff) / epsilon, 0.0 , 1.0);

             // Cook-Torrance BRDF
            float NDF = DistributionGGX(N, H, roughness);
            float G   = GeometrySmith(N, V, L, roughness);
            vec3 F    = fresnelSchlick(max(dot(H, V), 0.0), F0);

            vec3 numerator    = NDF * G * F;
            float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.0001; // + 0.0001 to prevent divide by zero
            vec3 specular = numerator / denominator;

             // kS is equal to Fresnel
             vec3 kS = F;
            // for energy conservation, the diffuse and specular light can't
            // be above 1.0 (unless the surface emits light); to preserve this
            // relationship the diffuse component (kD) should equal 1.0 - kS.
            vec3 kD = vec3(1.0) - kS;
            // multiply kD by the inverse metalness such that only non-metals
            // have diffuse lighting, or a linear blend if partly metal (pure metals
            // have no diffuse light).
            kD *= 1.0 - metallic;

            // scale light by NdotL
            float NdotL = max(dot(N, L), 0.0);

            Lo += (kD * albedo / PI + specular) * intensity * radiance * NdotL;

        }
        return  Lo;
}

