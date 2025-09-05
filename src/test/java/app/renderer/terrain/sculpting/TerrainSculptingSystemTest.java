package app.renderer.terrain.sculpting;

import app.ecs.Entity;
import app.ecs.EntitySystem;
import app.ecs.components.TerrainComponent;
import app.ecs.components.TerrainSculptingComponent;
import app.ecs.systems.SculptingSystem;
import app.math.components.Camera;
import app.math.components.OLTransform;
import app.math.OLVector3f;
import app.renderer.OpenGLObjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Terrain Sculpting System Tests")
public class TerrainSculptingSystemTest {

    @Mock
    private Camera mockCamera;
    
    @Mock
    private OpenGLObjects mockOpenGLObjects;
    
    @Mock
    private EntitySystem mockEntitySystem;
    
    private TerrainSculptingIntegration integration;
    private Entity testEntity;
    private TerrainComponent terrainComponent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create test entity with terrain component
        testEntity = new Entity("TestTerrain", new OLTransform());
        terrainComponent = new TerrainComponent(testEntity);
        testEntity.addComponent(terrainComponent);
        
        // Initialize integration
        integration = new TerrainSculptingIntegration();
    }

    @Test
    @DisplayName("Integration initializes correctly")
    void testIntegrationInitialization() {
        // Act
        integration.initialize(mockCamera, mockEntitySystem);
        
        // Assert
        assertTrue(integration.isInitialized(), "Integration should be initialized");
        assertNotNull(integration.getSculptingSystem(), "Sculpting system should be created");
        assertNotNull(integration.getSculptingWindow(), "Sculpting window should be created");
    }

    @Test
    @DisplayName("Sculpting can be enabled for terrain entity")
    void testEnableSculptingForTerrain() {
        // Arrange
        integration.initialize(mockCamera, mockEntitySystem);
        
        // Act
        integration.enableSculptingForTerrain(testEntity);
        
        // Assert
        assertTrue(testEntity.hasComponent(TerrainSculptingComponent.class), 
                   "Entity should have TerrainSculptingComponent");
        
        TerrainSculptingComponent sculptingComponent = 
            testEntity.getComponent(TerrainSculptingComponent.class);
        assertNotNull(sculptingComponent, "Sculpting component should not be null");
    }

    @Test
    @DisplayName("Sculpting fails for entity without terrain component")
    void testEnableSculptingFailsWithoutTerrainComponent() {
        // Arrange
        integration.initialize(mockCamera, mockEntitySystem);
        Entity entityWithoutTerrain = new Entity("NotTerrain", new OLTransform());
        
        // Act
        integration.enableSculptingForTerrain(entityWithoutTerrain);
        
        // Assert
        assertFalse(entityWithoutTerrain.hasComponent(TerrainSculptingComponent.class),
                   "Entity without terrain should not get sculpting component");
    }

    @Test
    @DisplayName("Sculpting can be disabled for terrain entity")
    void testDisableSculptingForTerrain() {
        // Arrange
        integration.initialize(mockCamera, mockEntitySystem);
        integration.enableSculptingForTerrain(testEntity);
        
        // Act
        integration.disableSculptingForTerrain(testEntity);
        
        // Assert
        TerrainSculptingComponent sculptingComponent = 
            testEntity.getComponent(TerrainSculptingComponent.class);
        if (sculptingComponent != null) {
            assertFalse(sculptingComponent.isActive(), 
                       "Sculpting component should be inactive");
        }
    }

    @Test
    @DisplayName("Integration fails when not initialized")
    void testOperationsFailWhenNotInitialized() {
        // Act & Assert
        assertFalse(integration.isInitialized(), "Integration should not be initialized");
        
        // These operations should not crash but also not work
        integration.enableSculptingForTerrain(testEntity);
        assertFalse(testEntity.hasComponent(TerrainSculptingComponent.class),
                   "Should not add component when not initialized");
    }

    @Test
    @DisplayName("Cleanup works correctly")
    void testCleanup() {
        // Arrange
        integration.initialize(mockCamera, mockEntitySystem);
        integration.enableSculptingForTerrain(testEntity);
        
        // Act
        integration.cleanUp();
        
        // Assert
        assertFalse(integration.isInitialized(), "Integration should not be initialized after cleanup");
    }
}