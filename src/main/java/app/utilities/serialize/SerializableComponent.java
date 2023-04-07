package app.utilities.serialize;

import app.ecs.Entity;
import app.ecs.components.*;
import app.math.OLVector3f;
import app.math.components.OLTransform;
import app.utilities.logger.LogError;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.nio.file.Path;
import java.util.Set;

class SerializableComponent {

    private static final String COMPONENT_NAME = "ComponentName";

    protected SerializableComponent() {
    }

    protected JsonArray serializableComponent(final Set<Component> components) {
        JsonArray componentsArray = new JsonArray();
        for (Component component : components) {
            componentsArray.add(saveComponentFactory(component));
        }
        return componentsArray;
    }

    private JsonObject saveComponentFactory(Component component) {
        JsonObject serializable = new JsonObject();
        switch (component) {
            case TransformComponent transform -> {
                serializable.addProperty(COMPONENT_NAME, TransformComponent.class.getSimpleName());
                serializable.add("Position", olVector3f(transform.getOlTransform().getPosition()));
                serializable.add("Rotation", olVector3f(transform.getOlTransform().getRotation()));
                serializable.add("Scale", olVector3f(transform.getOlTransform().getScale()));
            }
            case DirectionalLightComponent directionalLight -> {
                serializable.addProperty(COMPONENT_NAME, DirectionalLightComponent.class.getSimpleName());
                serializable.add("Direction", olVector3f(directionalLight.getDirectionalLight().getDirection()));
                serializable.add("Color", olVector3f(directionalLight.getDirectionalLight().getColor()));
                serializable.addProperty("LightIntensity", directionalLight.getDirectionalLight().getDirLightIntensity());
            }
            case PointLightComponent pointLight -> {
                serializable.addProperty(COMPONENT_NAME, PointLightComponent.class.getSimpleName());
                serializable.add("Position", olVector3f(pointLight.getPointLight().getPosition()));
                serializable.add("Color", olVector3f(pointLight.getPointLight().getColor()));
                serializable.addProperty("Quadratic", pointLight.getPointLight().getQuadratic());
                serializable.addProperty("Linear", pointLight.getPointLight().getLinear());
                serializable.addProperty("Constant", pointLight.getPointLight().getConstant());
            }
            case SpotLightComponent spotLight -> {
                serializable.addProperty(COMPONENT_NAME, SpotLightComponent.class.getSimpleName());
                serializable.add("Position", olVector3f(spotLight.getSpotLight().getPosition()));
                serializable.add("Direction", olVector3f(spotLight.getSpotLight().getDirection()));
                serializable.add("Color", olVector3f(spotLight.getSpotLight().getColor()));
                serializable.addProperty("Quadratic", spotLight.getSpotLight().getQuadratic());
                serializable.addProperty("Linear", spotLight.getSpotLight().getLinear());
                serializable.addProperty("Constant", spotLight.getSpotLight().getConstant());
                serializable.addProperty("CutOff", spotLight.getSpotLight().getCutOff());
                serializable.addProperty("OuterCutOff", spotLight.getSpotLight().getOuterCutOff());
            }
            case FogComponent fog -> {
                serializable.addProperty(COMPONENT_NAME, FogComponent.class.getSimpleName());
                serializable.add("Color", olVector3f(fog.getFog().getFogColor()));
                serializable.addProperty("SightRange", fog.getFog().getSightRange());
            }
            case TerrainComponent terrain -> {
                serializable.addProperty(COMPONENT_NAME, TerrainComponent.class.getSimpleName());
                serializable.addProperty("Path", terrain.getPath());
                serializable.addProperty("Wireframe", terrain.getWireframe().get());
                serializable.addProperty("Displacement", terrain.getTerrain().getDisplacementFactor());
                serializable.addProperty("MaterialTerrainAlbedo", terrain.getTerrain().getTerrainMaterial().getAlbedoMapPath());
                serializable.addProperty("MaterialTerrainNormal", terrain.getTerrain().getTerrainMaterial().getNormalMapPath());
                serializable.addProperty("MaterialTerrainRoughness", terrain.getTerrain().getTerrainMaterial().getRoughness());
            }
            case MusicComponent music -> {
                serializable.addProperty(COMPONENT_NAME, MusicComponent.class.getSimpleName());
                serializable.addProperty("Path", music.getPath());
            }
            case SoundEffectComponent soundEffect -> {
                serializable.addProperty(COMPONENT_NAME, SoundEffectComponent.class.getSimpleName());
                serializable.addProperty("Path", soundEffect.getPath());
                serializable.add("Position", olVector3f(soundEffect.getSoundEffect().getPosition()));
                serializable.add("Velocity", olVector3f(soundEffect.getSoundEffect().getVelocity()));
            }
            case SkyBoxComponent skyBox -> {
                serializable.addProperty(COMPONENT_NAME, SkyBoxComponent.class.getSimpleName());
                serializable.addProperty("Path", skyBox.getPath());
                serializable.addProperty("Exposure", skyBox.getSkyBox().getExposure());
            }
            case MeshComponent mesh -> {
                serializable.addProperty(COMPONENT_NAME, MeshComponent.class.getSimpleName());
                serializable.addProperty("MeshPath", mesh.getPath());
                serializable.addProperty("RenderType", mesh.getMeshRenderer().getSelect());
                serializable.addProperty("AlbedoPath", mesh.getMaterial().getAlbedoMapPath());
                serializable.addProperty("NormalPath", mesh.getMaterial().getNormalMapPath());
                serializable.addProperty("MetallicPath", mesh.getMaterial().getMetallicMapPath());
                serializable.addProperty("RoughnessPath", mesh.getMaterial().getRoughnessMapPath());
                serializable.addProperty("AoPath", mesh.getMaterial().getAoMapPath());
                serializable.addProperty("EmissivePath", mesh.getMaterial().getEmissiveMapPath());
            }
            default -> throw new IllegalStateException("Unexpected value: " + component);
        }
        return serializable;
    }

    private JsonArray olVector3f(OLVector3f olVector3f) {
        JsonArray vector3f = new JsonArray();
        vector3f.add(olVector3f.x);
        vector3f.add(olVector3f.y);
        vector3f.add(olVector3f.z);
        return vector3f;
    }

    public void deserializeComponent(JsonArray componentArray, final Entity entity) {
        for (int i = 0; i < componentArray.size(); i++) {
            JsonObject component = componentArray.get(i).getAsJsonObject();
            fillComponent(component, entity);
        }
    }

    private void fillComponent(JsonObject component, final Entity entity) {
        String componentName = component.get(COMPONENT_NAME).getAsString();
        switch (componentName) {
            case "TransformComponent" -> {
                OLTransform olTransform = new OLTransform();
                olTransform.setPosition(deOlVector3f(component.getAsJsonArray("Position")));
                olTransform.setRotation(deOlVector3f(component.getAsJsonArray("Rotation")));
                olTransform.setScale(deOlVector3f(component.getAsJsonArray("Scale")));
                TransformComponent transform = new TransformComponent(entity, olTransform);
                entity.addComponent(transform);
            }
            case "DirectionalLightComponent" -> {
                DirectionalLightComponent directionalLight = new DirectionalLightComponent(entity);
                directionalLight.getDirectionalLight().setDirection(deOlVector3f(component.getAsJsonArray("Direction")));
                JsonArray floatArray = component.getAsJsonArray("Color");
                directionalLight.getDirectionalLight().setColor(floatArray.get(0).getAsFloat(), floatArray.get(1).getAsFloat(), floatArray.get(2).getAsFloat());
                directionalLight.getDirectionalLight().setDirLightIntensity(component.get("LightIntensity").getAsFloat());
                entity.addComponent(directionalLight);
            }
            case "PointLightComponent" -> {
                PointLightComponent pointLight = new PointLightComponent(entity);
                pointLight.getPointLight().setPosition(deOlVector3f(component.getAsJsonArray("Position")));
                JsonArray floatArray = component.getAsJsonArray("Color");
                pointLight.getPointLight().setColor(floatArray.get(0).getAsFloat(), floatArray.get(1).getAsFloat(), floatArray.get(2).getAsFloat());
                pointLight.getPointLight().setLinear(component.get("Linear").getAsFloat());
                pointLight.getPointLight().setQuadratic(component.get("Quadratic").getAsFloat());
                pointLight.getPointLight().setConstant(component.get("Constant").getAsFloat());
                entity.addComponent(pointLight);
            }
            case "SpotLightComponent" -> {
                SpotLightComponent spotLight = new SpotLightComponent(entity);
                spotLight.getSpotLight().setPosition(deOlVector3f(component.getAsJsonArray("Position")));
                spotLight.getSpotLight().setDirection(deOlVector3f(component.getAsJsonArray("Direction")));
                JsonArray floatArray = component.getAsJsonArray("Color");
                spotLight.getSpotLight().setColor(floatArray.get(0).getAsFloat(), floatArray.get(1).getAsFloat(), floatArray.get(2).getAsFloat());
                spotLight.getSpotLight().setLinear(component.get("Linear").getAsFloat());
                spotLight.getSpotLight().setQuadratic(component.get("Quadratic").getAsFloat());
                spotLight.getSpotLight().setConstant(component.get("Constant").getAsFloat());
                spotLight.getSpotLight().setCutOff(component.get("CutOff").getAsFloat());
                spotLight.getSpotLight().setOuterCutOff(component.get("OuterCutOff").getAsFloat());
                entity.addComponent(spotLight);
            }
            case "FogComponent" -> {
                FogComponent fog = new FogComponent(entity);
                JsonArray floatArray = component.getAsJsonArray("Color");
                fog.getFog().setFogColor(floatArray.get(0).getAsFloat(), floatArray.get(1).getAsFloat(), floatArray.get(2).getAsFloat());
                fog.getFog().setSightRange(component.get("SightRange").getAsFloat());
                entity.addComponent(fog);
            }
            case "TerrainComponent" -> {
                TerrainComponent terrain = new TerrainComponent(entity);
                terrain.setPath(component.get("Path").getAsString());
                terrain.getWireframe().set(component.get("Wireframe").getAsBoolean());
                terrain.getTerrain().setDisplacementFactor(component.get("Displacement").getAsFloat());
                terrain.getTerrain().init(Path.of(terrain.getPath()));
                terrain.getTerrain().setActive(true);
                terrain.getTerrain().getTerrainMaterial().setRoughness(component.get("MaterialTerrainRoughness").getAsFloat());
                terrain.getTerrain().getTerrainMaterial().setAlbedoMap(component.get("MaterialTerrainAlbedo").getAsString());
                terrain.getTerrain().getTerrainMaterial().setNormalMap(component.get("MaterialTerrainNormal").getAsString());
                entity.addComponent(terrain);
            }
            case "MusicComponent" -> {
                MusicComponent music = new MusicComponent(entity);
                music.setPath(component.get("Path").getAsString());
                entity.addComponent(music);
            }
            case "SoundEffectComponent" -> {
                SoundEffectComponent soundEffect = new SoundEffectComponent(entity);
                soundEffect.setPath(component.get("Path").getAsString());
                soundEffect.getSoundEffect().setPosition(deOlVector3f(component.getAsJsonArray("Position")));
                soundEffect.getSoundEffect().setVelocity(deOlVector3f(component.getAsJsonArray("Velocity")));
                entity.addComponent(soundEffect);
            }
            case "SkyBoxComponent" -> {
                SkyBoxComponent skyBox = new SkyBoxComponent(entity);
                skyBox.setPath(component.get("Path").getAsString());
                skyBox.getSkyBox().setExposure(component.get("Exposure").getAsFloat());
                skyBox.getSkyBox().init(Path.of(skyBox.getPath()));
                entity.addComponent(skyBox);
            }
            case "MeshComponent" -> {
                MeshComponent mesh = new MeshComponent(entity);
                mesh.setPath(component.get("MeshPath").getAsString(), true);
                mesh.getMaterial().setAlbedoMap(component.get("AlbedoPath").getAsString());
                mesh.getMaterial().setNormalMap(component.get("NormalPath").getAsString());
                mesh.getMaterial().setMetallicMap(component.get("MetallicPath").getAsString());
                mesh.getMaterial().setRoughnessMap(component.get("RoughnessPath").getAsString());
                mesh.getMaterial().setEmissiveMap(component.get("EmissivePath").getAsString());
                mesh.getMaterial().setAoMap(component.get("AoPath").getAsString());
                mesh.getMeshRenderer().setSelect(component.get("RenderType").getAsInt());
                entity.addComponent(mesh);
            }
            default -> LogError.println("cant find this component " + componentName);
        }
    }

    private OLVector3f deOlVector3f(JsonArray vector3f) {
        return new OLVector3f(vector3f.get(0).getAsFloat(), vector3f.get(1).getAsFloat(), vector3f.get(2).getAsFloat());
    }
}
