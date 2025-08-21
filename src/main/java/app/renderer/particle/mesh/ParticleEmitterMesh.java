package app.renderer.particle.mesh;

import app.math.MathUtil;
import app.math.OLVector3f;
import app.math.components.Camera;
import app.renderer.OpenGLObjects;
import app.renderer.Textures;
import app.renderer.VaoModel;
import app.renderer.ibl.SkyBox;
import app.renderer.lights.LightHandler;
import app.renderer.pbr.Mesh;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ParticleEmitterMesh {
    private final ParticleRenderer particleRenderer;
    private final List<ParticleMesh> particles = new ArrayList<>();
    private VaoModel vaoModel;
    private boolean pause = false;
    private boolean play = false;
    
    private OLVector3f emitterPosition = new OLVector3f(0, 0, 0);
    private OLVector3f positionVariance = new OLVector3f(1, 1, 1);
    private OLVector3f velocityMin = new OLVector3f(-1, 0, -1);
    private OLVector3f velocityMax = new OLVector3f(1, 2, 1);
    private OLVector3f rotation = new OLVector3f(0, 0, 0);
    private OLVector3f scale = new OLVector3f(1, 1, 1);
    private float gravityEffect = -9.81f;
    private float lifeLength = 5.0f;
    private boolean infinity = false;
    
    public ParticleEmitterMesh(Camera camera, OpenGLObjects openGLObjects, Textures textures, 
                              SkyBox skyBox, LightHandler lightHandler) {
        particleRenderer = new ParticleRenderer(camera, openGLObjects, textures, skyBox, lightHandler);
    }
    
    public void init(Mesh mesh) {
        particleRenderer.init(mesh, null);
        vaoModel = particleRenderer.getVaoModel();
    }
    
    public void createParticles(int amount) {
        particles.clear();
        for (int i = 0; i < amount; i++) {
            OLVector3f position = MathUtil.getRandomNumber(
                emitterPosition.sub(positionVariance),
                emitterPosition.add(positionVariance)
            );
            OLVector3f velocity = MathUtil.getRandomNumber(velocityMin, velocityMax);
            
            ParticleMesh particle = new ParticleMesh(
                position, velocity, gravityEffect, lifeLength, 
                rotation, scale, infinity
            );
            particles.add(particle);
        }
    }
    
    public void update(float dt) {
        if (!pause && play) {
            particles.removeIf(particle -> !particle.update(dt));
            
            if (infinity && particles.isEmpty()) {
                createParticles((int) particleRenderer.getMaterial().getAmount());
            }
        }
    }
    
    public void render() {
        if (play && vaoModel != null) {
            particleRenderer.renderParticles(particles, vaoModel);
        }
    }
    
    public void reset() {
        play = false;
        pause = true;
        particles.clear();
    }
    
    public void cleanUp() {
        particles.clear();
    }
    
    public void setPause(boolean pause) {
        this.pause = pause;
    }
    
    public void setPlay(boolean play) {
        this.play = play;
    }
    
    public void setInfinity(boolean infinity) {
        this.infinity = infinity;
        particles.forEach(p -> p.setInfinity(infinity));
    }
    
    public ParticleRenderer getParticleRenderer() {
        return particleRenderer;
    }
    
    public OLVector3f getEmitterPosition() {
        return emitterPosition;
    }
    
    public void setEmitterPosition(OLVector3f emitterPosition) {
        this.emitterPosition = emitterPosition;
    }
    
    public OLVector3f getPositionVariance() {
        return positionVariance;
    }
    
    public void setPositionVariance(OLVector3f positionVariance) {
        this.positionVariance = positionVariance;
    }
    
    public OLVector3f getVelocityMin() {
        return velocityMin;
    }
    
    public void setVelocityMin(OLVector3f velocityMin) {
        this.velocityMin = velocityMin;
    }
    
    public OLVector3f getVelocityMax() {
        return velocityMax;
    }
    
    public void setVelocityMax(OLVector3f velocityMax) {
        this.velocityMax = velocityMax;
    }
    
    public float getGravityEffect() {
        return gravityEffect;
    }
    
    public void setGravityEffect(float gravityEffect) {
        this.gravityEffect = gravityEffect;
        particles.forEach(p -> p.setGravityEffect(gravityEffect));
    }
    
    public float getLifeLength() {
        return lifeLength;
    }
    
    public void setLifeLength(float lifeLength) {
        this.lifeLength = lifeLength;
        particles.forEach(p -> p.setLifeLength(lifeLength));
    }
    
    public OLVector3f getRotation() {
        return rotation;
    }
    
    public void setRotation(OLVector3f rotation) {
        this.rotation = rotation;
        particles.forEach(p -> p.setRotation(rotation));
    }
    
    public OLVector3f getScale() {
        return scale;
    }
    
    public void setScale(OLVector3f scale) {
        this.scale = scale;
        particles.forEach(p -> p.setScale(scale));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticleEmitterMesh that = (ParticleEmitterMesh) o;
        return Objects.equals(this, that);
    }
}