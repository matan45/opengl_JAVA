package app.utilities.serialize;

import app.ecs.Entity;
import app.ecs.components.*;
import app.math.OLVector3f;
import app.math.components.OLTransform;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
                serializable.add("Direction", olVector3f(directionalLight.getDirectionalLight().getDirection()));//check if needed optional
                serializable.add("Color", olVector3f(directionalLight.getDirectionalLight().getColor()));
                serializable.addProperty("LightIntensity", directionalLight.getDirectionalLight().getDirLightIntensity());
            }
            case PointLightComponent pointLight -> {
                serializable.addProperty(COMPONENT_NAME, PointLightComponent.class.getSimpleName());
                serializable.add("Position", olVector3f(pointLight.getPointLight().getPosition()));//check if needed optional
                serializable.add("Color", olVector3f(pointLight.getPointLight().getColor()));
                serializable.addProperty("Quadratic", pointLight.getPointLight().getQuadratic());
                serializable.addProperty("Linear", pointLight.getPointLight().getLinear());
                serializable.addProperty("Constant", pointLight.getPointLight().getConstant());
            }
            case SpotLightComponent SpotLight -> {
                serializable.addProperty(COMPONENT_NAME, SpotLightComponent.class.getSimpleName());
                serializable.add("Position", olVector3f(SpotLight.getSpotLight().getPosition()));//check if needed optional
                serializable.add("Direction", olVector3f(SpotLight.getSpotLight().getDirection()));//check if needed optional
                serializable.add("Color", olVector3f(SpotLight.getSpotLight().getColor()));
                serializable.addProperty("Quadratic", SpotLight.getSpotLight().getQuadratic());
                serializable.addProperty("Linear", SpotLight.getSpotLight().getLinear());
                serializable.addProperty("Constant", SpotLight.getSpotLight().getConstant());
                serializable.addProperty("CutOff", SpotLight.getSpotLight().getCutOff());
                serializable.addProperty("OuterCutOff", SpotLight.getSpotLight().getOuterCutOff());
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
            }
            case MusicComponent music -> {
                serializable.addProperty(COMPONENT_NAME, MusicComponent.class.getSimpleName());
                serializable.addProperty("Path", music.getPath());
            }
            case SoundEffectComponent soundEffect -> {
                serializable.addProperty(COMPONENT_NAME, SoundEffectComponent.class.getSimpleName());
                serializable.addProperty("Path", soundEffect.getPath());
                serializable.add("Position", olVector3f(soundEffect.getSoundEffect().getPosition()));//check if needed optional
                serializable.add("Velocity", olVector3f(soundEffect.getSoundEffect().getVelocity()));//check if needed optional
            }
            case SkyBoxComponent skyBox -> {
                serializable.addProperty(COMPONENT_NAME, SkyBoxComponent.class.getSimpleName());
                serializable.addProperty("Path", skyBox.getPath());
                serializable.addProperty("Exposure", skyBox.getSkyBox().getExposure());
            }
            case MeshComponent mesh -> {
                serializable.addProperty(COMPONENT_NAME, MeshComponent.class.getSimpleName());
                serializable.addProperty("MeshPath", mesh.getPath());
                serializable.addProperty("AlbedoPath", mesh.getMaterial().getAlbedoMapPath());
                serializable.addProperty("NormalPath", mesh.getMaterial().getNormalMapPath());
                serializable.addProperty("MetallicPath", mesh.getMaterial().getMetallicMapPath());
                serializable.addProperty("Roughness", mesh.getMaterial().getRoughnessMapPath());
                serializable.addProperty("EmissivePath", mesh.getMaterial().getEmissiveMapPath());
                serializable.addProperty("AoPath", mesh.getMaterial().getAoMapPath());
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

    public void deserializeComponent(JsonArray componentJson, Entity entity) {
        for (int i = 0; i < componentJson.size(); i++) {
            JsonObject component = componentJson.get(i).getAsJsonObject();
            fillComponent(component, entity);
        }
    }

    private void fillComponent(JsonObject component, Entity entity) {
        String componentName = component.get(COMPONENT_NAME).getAsString();
        switch (componentName) {
            case "TransformComponent": {
                OLTransform olTransform = new OLTransform();
                TransformComponent transform = new TransformComponent(entity, olTransform);
                entity.addComponent(transform);
                break;
            }


        }
    }
}
