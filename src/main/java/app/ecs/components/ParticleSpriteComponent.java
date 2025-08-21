package app.ecs.components;

import app.ecs.Entity;
import app.math.OLVector3f;
import app.renderer.particle.sprite.ParticleEmitterSprite;
import app.renderer.particle.sprite.ParticleSystemSprite;
import app.renderer.particle.sprite.data.ParticleMaterialSprite;
import app.renderer.particle.sprite.data.ParticlePositionSprite;
import app.renderer.particle.sprite.data.ParticleVelocitySprite;
import app.utilities.OpenFileDialog;
import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImInt;

import java.nio.file.Path;
import java.util.Optional;

public class ParticleSpriteComponent extends Component {
    private ParticleEmitterSprite emitter;
    private ParticleMaterialSprite particleMaterial;
    
    private final ImFloat lifeLength;
    private final ImInt particleAmount;
    private final ImFloat gravityEffect;
    private final ImBoolean infinity;
    private final ImBoolean play;
    private final ImBoolean pause;
    
    private final float[] positionMin = new float[3];
    private final float[] positionMax = new float[3];
    private final float[] velocityMin = new float[3];
    private final float[] velocityMax = new float[3];
    private final float[] scale = new float[3];
    private final float[] rotation = new float[3];
    
    private String texturePath = "";
    private String textureFileName = "No texture";

    public ParticleSpriteComponent(Entity ownerEntity) {
        super(ownerEntity);
        
        particleMaterial = new ParticleMaterialSprite();
        emitter = ParticleSystemSprite.createEmitter();
        
        lifeLength = new ImFloat(5.0f);
        particleAmount = new ImInt(100);
        gravityEffect = new ImFloat(0.0f);
        infinity = new ImBoolean(false);
        play = new ImBoolean(false);
        pause = new ImBoolean(false);
        
        scale[0] = scale[1] = scale[2] = 1.0f;
    }

    @Override
    public void imguiDraw() {
        ImGui.separator();
        ImGui.text("Particle Sprite Settings");
        ImGui.separator();
        
        if (ImGui.checkbox("Play", play)) {
            emitter.setPlay(play.get());
            if (play.get() && emitter != null) {
                updateParticleMaterial();
                emitter.createParticle(particleMaterial);
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
        ImGui.text("Particle Properties");
        
        if (ImGui.dragFloat("Life Length", lifeLength.getData(), 0.1f, 0.1f, 100.0f)) {
            particleMaterial.setLifeLength(lifeLength.get());
        }
        
        if (ImGui.dragInt("Particle Amount", particleAmount.getData(), 1, 1, 10000)) {
            particleMaterial.setParticleAmount(particleAmount.get());
        }
        
        if (ImGui.dragFloat("Gravity Effect", gravityEffect.getData(), 0.01f, -10.0f, 10.0f)) {
            particleMaterial.setGravityEffect(gravityEffect.get());
        }
        
        if (ImGui.checkbox("Infinity", infinity)) {
            particleMaterial.setInfinity(infinity.get());
            emitter.setInfinity(infinity.get());
        }
        
        ImGui.separator();
        ImGui.text("Texture");
        if (ImGui.button("Select Texture")) {
            Optional<Path> path = OpenFileDialog.openFile("png,jpg,tga", "Texture");
            if (path.isPresent()) {
                texturePath = path.get().toString();
                textureFileName = path.get().getFileName().toString();
                particleMaterial.setTexturePath(texturePath);
            }
        }
        ImGui.sameLine();
        ImGui.text(textureFileName);
        
        ImGui.separator();
        ImGui.text("Position Range");
        if (ImGui.dragFloat3("Position Min", positionMin, 0.1f)) {
            updatePositionRange();
        }
        if (ImGui.dragFloat3("Position Max", positionMax, 0.1f)) {
            updatePositionRange();
        }
        
        ImGui.separator();
        ImGui.text("Velocity Range");
        if (ImGui.dragFloat3("Velocity Min", velocityMin, 0.1f)) {
            updateVelocityRange();
        }
        if (ImGui.dragFloat3("Velocity Max", velocityMax, 0.1f)) {
            updateVelocityRange();
        }
        
        ImGui.separator();
        ImGui.text("Transform");
        if (ImGui.dragFloat3("Scale", scale, 0.01f, 0.01f, 10.0f)) {
            particleMaterial.setScale(new OLVector3f(scale[0], scale[1], scale[2]));
        }
        if (ImGui.dragFloat3("Rotation", rotation, 1.0f, 0.0f, 360.0f)) {
            particleMaterial.setRotation(new OLVector3f(rotation[0], rotation[1], rotation[2]));
        }
    }
    
    private void updatePositionRange() {
        ParticlePositionSprite positionSprite = particleMaterial.getParticlePosition();
        positionSprite.setMinOffsetPosition(new OLVector3f(positionMin[0], positionMin[1], positionMin[2]));
        positionSprite.setMaxOffsetPosition(new OLVector3f(positionMax[0], positionMax[1], positionMax[2]));
    }
    
    private void updateVelocityRange() {
        ParticleVelocitySprite velocitySprite = particleMaterial.getParticleVelocity();
        velocitySprite.setMinOffsetVelocity(new OLVector3f(velocityMin[0], velocityMin[1], velocityMin[2]));
        velocitySprite.setMaxOffsetVelocity(new OLVector3f(velocityMax[0], velocityMax[1], velocityMax[2]));
    }
    
    private void updateParticleMaterial() {
        particleMaterial.setLifeLength(lifeLength.get());
        particleMaterial.setParticleAmount(particleAmount.get());
        particleMaterial.setGravityEffect(gravityEffect.get());
        particleMaterial.setInfinity(infinity.get());
        particleMaterial.setTexturePath(texturePath);
        updatePositionRange();
        updateVelocityRange();
        particleMaterial.setScale(new OLVector3f(scale[0], scale[1], scale[2]));
        particleMaterial.setRotation(new OLVector3f(rotation[0], rotation[1], rotation[2]));
    }

    @Override
    public void cleanUp() {
        if (emitter != null) {
            ParticleSystemSprite.remove(emitter);
            emitter = null;
        }
    }
    
    public ParticleEmitterSprite getEmitter() {
        return emitter;
    }
    
    public ParticleMaterialSprite getParticleMaterial() {
        return particleMaterial;
    }
}
