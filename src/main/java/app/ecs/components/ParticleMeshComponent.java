package app.ecs.components;

import app.ecs.Entity;
import app.math.OLVector3f;
import app.math.components.OLTransform;
import app.renderer.draw.EditorRenderer;
import app.renderer.particle.mesh.ParticleEmitterMesh;
import app.renderer.particle.mesh.ParticleMaterial;
import app.renderer.particle.mesh.ParticleSystemMesh;
import app.renderer.pbr.Mesh;
import app.utilities.OpenFileDialog;
import app.utilities.resource.ResourceManager;
import app.utilities.serialize.FileExtension;
import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImInt;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public class ParticleMeshComponent extends Component{
    private ParticleEmitterMesh emitter;
    private final ParticleMaterial particleMaterial;
    private final OLTransform olTransform;
    
    private String meshPath = "";
    private String preMeshPath = "";
    private File meshFile;
    private Mesh currentMesh;
    
    private final ImInt particleAmount;
    private final ImFloat lifeLength;
    private final ImFloat gravityEffect;
    private final ImBoolean infinity;
    private final ImBoolean play;
    private final ImBoolean pause;
    
    private final float[] positionVariance = new float[3];
    private final float[] velocityMin = new float[3];
    private final float[] velocityMax = new float[3];
    private final float[] rotation = new float[3];
    private final float[] scale = new float[3];
    
    private final ImFloat metallic;
    private final ImFloat roughness;
    private final ImFloat ao;
    private final ImFloat emissive;
    
    public ParticleMeshComponent(Entity ownerEntity) {
        super(ownerEntity);
        
        emitter = ParticleSystemMesh.createEmitter();
        particleMaterial = emitter.getParticleRenderer().getMaterial();
        olTransform = ownerEntity.getComponent(TransformComponent.class).getOlTransform();
        meshFile = new File("");
        
        particleAmount = new ImInt(10);
        lifeLength = new ImFloat(5.0f);
        gravityEffect = new ImFloat(-9.81f);
        infinity = new ImBoolean(false);
        play = new ImBoolean(false);
        pause = new ImBoolean(false);
        
        positionVariance[0] = positionVariance[1] = positionVariance[2] = 1.0f;
        velocityMin[0] = velocityMin[2] = -1.0f;
        velocityMin[1] = 0.0f;
        velocityMax[0] = velocityMax[2] = 1.0f;
        velocityMax[1] = 2.0f;
        scale[0] = scale[1] = scale[2] = 1.0f;
        
        metallic = new ImFloat(0.0f);
        roughness = new ImFloat(0.5f);
        ao = new ImFloat(1.0f);
        emissive = new ImFloat(0.0f);
    }

    @Override
    public void imguiDraw() {
        ImGui.separator();
        ImGui.text("Particle Mesh Settings");
        ImGui.separator();
        
        if (ImGui.checkbox("Play", play)) {
            if (play.get() && currentMesh != null) {
                updateEmitterProperties();
                emitter.createParticles(particleAmount.get());
                emitter.setPlay(true);
            } else {
                emitter.setPlay(false);
            }
        }
        
        ImGui.sameLine();
        if (ImGui.checkbox("Pause", pause)) {
            emitter.setPause(pause.get());
        }
        
        ImGui.sameLine();
        if (ImGui.button("Reset")) {
            emitter.reset();
            play.set(false);
            pause.set(false);
        }
        
        ImGui.separator();
        ImGui.text("Mesh Selection");
        if (ImGui.button("Select Mesh"))
            meshPath = OpenFileDialog.openFile(FileExtension.MESH_EXTENSION.getFileName(),"Mesh").orElse(Path.of(preMeshPath)).toString();
        
        if (!meshPath.isEmpty() && !preMeshPath.equals(meshPath))
            initMesh(false);
        
        ImGui.sameLine();
        ImGui.textWrapped(meshFile.getName());
        
        ImGui.separator();
        ImGui.text("Particle Properties");
        
        if (ImGui.dragInt("Particle Amount", particleAmount.getData(), 1, 1, 1000)) {
            particleMaterial.setAmount(particleAmount.get());
        }
        
        if (ImGui.dragFloat("Life Length", lifeLength.getData(), 0.1f, 0.1f, 100.0f)) {
            emitter.setLifeLength(lifeLength.get());
        }
        
        if (ImGui.dragFloat("Gravity Effect", gravityEffect.getData(), 0.1f, -50.0f, 50.0f)) {
            emitter.setGravityEffect(gravityEffect.get());
        }
        
        if (ImGui.checkbox("Infinity", infinity)) {
            emitter.setInfinity(infinity.get());
        }
        
        ImGui.separator();
        ImGui.text("Spawn Settings");
        
        if (ImGui.dragFloat3("Position Variance", positionVariance, 0.1f)) {
            emitter.setPositionVariance(new OLVector3f(positionVariance[0], positionVariance[1], positionVariance[2]));
        }
        
        if (ImGui.dragFloat3("Velocity Min", velocityMin, 0.1f)) {
            emitter.setVelocityMin(new OLVector3f(velocityMin[0], velocityMin[1], velocityMin[2]));
        }
        
        if (ImGui.dragFloat3("Velocity Max", velocityMax, 0.1f)) {
            emitter.setVelocityMax(new OLVector3f(velocityMax[0], velocityMax[1], velocityMax[2]));
        }
        
        ImGui.separator();
        ImGui.text("Transform");
        
        if (ImGui.dragFloat3("Rotation", rotation, 1.0f, 0.0f, 360.0f)) {
            emitter.setRotation(new OLVector3f(rotation[0], rotation[1], rotation[2]));
        }
        
        if (ImGui.dragFloat3("Scale", scale, 0.01f, 0.01f, 10.0f)) {
            emitter.setScale(new OLVector3f(scale[0], scale[1], scale[2]));
        }
        
        ImGui.separator();
        ImGui.text("Material Properties");
        
        if (ImGui.sliderFloat("Metallic", metallic.getData(), 0.0f, 1.0f)) {
            particleMaterial.setMetallic(metallic.get());
        }
        
        if (ImGui.sliderFloat("Roughness", roughness.getData(), 0.0f, 1.0f)) {
            particleMaterial.setRoughness(roughness.get());
        }
        
        if (ImGui.sliderFloat("Ambient Occlusion", ao.getData(), 0.0f, 1.0f)) {
            particleMaterial.setAo(ao.get());
        }
        
        if (ImGui.sliderFloat("Emissive", emissive.getData(), 0.0f, 10.0f)) {
            particleMaterial.setEmissive(emissive.get());
        }
        
        ImGui.separator();
        ImGui.text("Textures");
        ImGui.columns(3, "", true);
        
        particleMaterial.setAlbedoMap(materialPath("Albedo"));
        ImGui.nextColumn();
        ImGui.textWrapped(particleMaterial.getAlbedoFileName());
        ImGui.nextColumn();
        ImGui.pushID("Albedo");
        if (ImGui.button("X"))
            particleMaterial.albedoMapRemove();
        ImGui.popID();
        
        ImGui.nextColumn();
        particleMaterial.setNormalMap(materialPath("Normal"));
        ImGui.nextColumn();
        ImGui.textWrapped(particleMaterial.getNormalFileName());
        ImGui.nextColumn();
        ImGui.pushID("Normal");
        if (ImGui.button("X"))
            particleMaterial.normalMapRemove();
        ImGui.popID();
        
        ImGui.columns(1);
    }
    
    private String materialPath(String buttonName) {
        if (ImGui.button(buttonName)) {
            Optional<Path> materialPath = OpenFileDialog.openFile("png,tga,jpg","Texture");
            return materialPath.orElse(Path.of("")).toString();
        }
        return "";
    }
    
    private void initMesh(boolean useTransform) {
        preMeshPath = meshPath;
        meshFile = new File(meshPath);
        currentMesh = ResourceManager.loadMeshFromFile(Path.of(meshPath));
        if (!useTransform)
            olTransform.setPosition(currentMesh.center());
        
        emitter.init(currentMesh);
        emitter.setEmitterPosition(olTransform.getPosition());
    }
    
    private void updateEmitterProperties() {
        emitter.setEmitterPosition(olTransform.getPosition());
        emitter.setPositionVariance(new OLVector3f(positionVariance[0], positionVariance[1], positionVariance[2]));
        emitter.setVelocityMin(new OLVector3f(velocityMin[0], velocityMin[1], velocityMin[2]));
        emitter.setVelocityMax(new OLVector3f(velocityMax[0], velocityMax[1], velocityMax[2]));
        emitter.setRotation(new OLVector3f(rotation[0], rotation[1], rotation[2]));
        emitter.setScale(new OLVector3f(scale[0], scale[1], scale[2]));
        emitter.setLifeLength(lifeLength.get());
        emitter.setGravityEffect(gravityEffect.get());
        emitter.setInfinity(infinity.get());
    }

    @Override
    public void cleanUp() {
        if (emitter != null) {
            ParticleSystemMesh.remove(emitter);
            emitter = null;
        }
    }
    
    public ParticleEmitterMesh getEmitter() {
        return emitter;
    }
    
    public ParticleMaterial getParticleMaterial() {
        return particleMaterial;
    }
    
    public String getMeshPath() {
        return meshPath;
    }
    
    public void setMeshPath(String meshPath, boolean useTransform) {
        this.meshPath = meshPath;
        initMesh(useTransform);
    }
    
    public Mesh getCurrentMesh() {
        return currentMesh;
    }
}
