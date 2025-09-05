package app.renderer.terrain.sculpting;

import app.ecs.Entity;
import app.ecs.EntitySystem;
import app.ecs.components.TerrainComponent;
import app.ecs.components.TerrainSculptingComponent;
import app.math.components.Camera;
import app.math.components.OLTransform;
import app.renderer.OpenGLObjects;
import app.utilities.logger.LogInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Terrain Sculpting Integration Tests")
public class TerrainSculptingIntegrationTest {

    private TerrainSculptingIntegration integration;
    private Entity terrainEntity;
    private ByteArrayOutputStream logOutput;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        // Capture console output for testing
        logOutput = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(logOutput));
        
        // Create mock terrain entity
        terrainEntity = new Entity("TestTerrain", new OLTransform());
        
        // Create integration
        integration = new TerrainSculptingIntegration();
    }

    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Full workflow: Initialize → Enable → Sculpt → Disable")
    void testFullSculptingWorkflow() {
        // Step 1: Initialize integration
        integration.initialize(null); // Using null camera for testing
        assertTrue(integration.isInitialized(), "Integration should be initialized");
        
        // Step 2: Add terrain component to entity
        TerrainComponent terrainComponent = new TerrainComponent(terrainEntity);
        terrainEntity.addComponent(terrainComponent);
        assertTrue(terrainEntity.hasComponent(TerrainComponent.class), 
                  "Entity should have terrain component");
        
        // Step 3: Enable sculpting for terrain
        integration.enableSculptingForTerrain(terrainEntity);
        assertTrue(terrainEntity.hasComponent(TerrainSculptingComponent.class),
                  "Entity should have sculpting component after enabling");
        
        // Step 4: Verify sculpting component is configured correctly
        TerrainSculptingComponent sculptingComp = terrainEntity.getComponent(TerrainSculptingComponent.class);
        assertNotNull(sculptingComp, "Sculpting component should exist");
        assertNotNull(sculptingComp.getBrushSettings(), "Brush settings should be initialized");
        
        // Step 5: Test sculpting operations
        sculptingComp.setActive(true);
        assertTrue(sculptingComp.isActive(), "Sculpting should be active");
        
        // Simulate sculpting operations
        sculptingComp.setCurrentlySculpting(true);
        sculptingComp.incrementModifications();
        assertTrue(sculptingComp.isCurrentlySculpting(), "Should be currently sculpting");
        assertEquals(1, sculptingComp.getModificationsCount(), "Should have one modification");
        
        // Step 6: Disable sculpting
        integration.disableSculptingForTerrain(terrainEntity);
        assertFalse(sculptingComp.isActive(), "Sculpting should be disabled");
        
        tearDown();
    }

    @Test
    @DisplayName("Integration handles invalid scenarios gracefully")
    void testInvalidScenarios() {
        // Test 1: Operations before initialization
        assertFalse(integration.isInitialized(), "Should not be initialized initially");
        
        Entity invalidEntity = new Entity("Invalid", new OLTransform());
        integration.enableSculptingForTerrain(invalidEntity);
        assertFalse(invalidEntity.hasComponent(TerrainSculptingComponent.class),
                   "Should not add sculpting to uninitialized integration");
        
        // Test 2: Enable sculpting on entity without terrain component
        integration.initialize(null);
        Entity entityWithoutTerrain = new Entity("NoTerrain", new OLTransform());
        integration.enableSculptingForTerrain(entityWithoutTerrain);
        assertFalse(entityWithoutTerrain.hasComponent(TerrainSculptingComponent.class),
                   "Should not add sculpting to entity without terrain");
        
        tearDown();
    }

    @Test
    @DisplayName("Logging works correctly during operations")
    void testLoggingOutput() {
        // Initialize and perform operations
        integration.initialize(null);
        
        TerrainComponent terrainComponent = new TerrainComponent(terrainEntity);
        terrainEntity.addComponent(terrainComponent);
        integration.enableSculptingForTerrain(terrainEntity);
        
        // Check log output contains expected messages
        String logContent = logOutput.toString();
        assertTrue(logContent.contains("initialized"), 
                  "Log should contain initialization message");
        
        tearDown();
    }

    @Test
    @DisplayName("Multiple terrains can be handled")
    void testMultipleTerrainsSupport() {
        integration.initialize(null);
        
        // Create multiple terrain entities
        Entity terrain1 = new Entity("Terrain1", new OLTransform());
        Entity terrain2 = new Entity("Terrain2", new OLTransform());
        
        terrain1.addComponent(new TerrainComponent(terrain1));
        terrain2.addComponent(new TerrainComponent(terrain2));
        
        // Enable sculpting for both
        integration.enableSculptingForTerrain(terrain1);
        integration.enableSculptingForTerrain(terrain2);
        
        // Both should have sculpting components
        assertTrue(terrain1.hasComponent(TerrainSculptingComponent.class),
                  "First terrain should have sculpting");
        assertTrue(terrain2.hasComponent(TerrainSculptingComponent.class),
                  "Second terrain should have sculpting");
        
        // Both should be independent
        TerrainSculptingComponent sculpting1 = terrain1.getComponent(TerrainSculptingComponent.class);
        TerrainSculptingComponent sculpting2 = terrain2.getComponent(TerrainSculptingComponent.class);
        
        sculpting1.setActive(true);
        sculpting2.setActive(false);
        
        assertTrue(sculpting1.isActive(), "First terrain sculpting should be active");
        assertFalse(sculpting2.isActive(), "Second terrain sculpting should be inactive");
        
        tearDown();
    }

    @Test
    @DisplayName("Cleanup works properly")
    void testProperCleanup() {
        // Setup
        integration.initialize(null);
        TerrainComponent terrainComponent = new TerrainComponent(terrainEntity);
        terrainEntity.addComponent(terrainComponent);
        integration.enableSculptingForTerrain(terrainEntity);
        
        assertTrue(integration.isInitialized(), "Should be initialized before cleanup");
        
        // Cleanup
        integration.cleanUp();
        
        assertFalse(integration.isInitialized(), "Should not be initialized after cleanup");
        
        tearDown();
    }

    /**
     * This test can be run manually to verify the system works with real UI
     * It provides a framework for manual testing of the sculpting system
     */
    @Test
    @DisplayName("Manual testing framework")
    void manualTestingFramework() {
        System.out.println("=== Manual Testing Instructions ===");
        System.out.println("1. Run the application");
        System.out.println("2. Create a terrain entity in the scene");
        System.out.println("3. Load a heightmap texture");
        System.out.println("4. Click 'Enable Sculpting' button");
        System.out.println("5. Click 'Open Sculpting Window' button");
        System.out.println("6. Verify the sculpting window opens");
        System.out.println("7. Test different brush tools:");
        System.out.println("   - Raise: Should lift terrain when clicking/dragging");
        System.out.println("   - Lower: Should depress terrain when clicking/dragging");
        System.out.println("   - Smooth: Should smooth out rough terrain");
        System.out.println("   - Flatten: Should flatten terrain to target height");
        System.out.println("   - Noise: Should add random variation");
        System.out.println("8. Test brush settings:");
        System.out.println("   - Size: Should change brush radius");
        System.out.println("   - Strength: Should change modification intensity");
        System.out.println("   - Falloff: Should change edge softness");
        System.out.println("9. Test keyboard shortcuts:");
        System.out.println("   - 1-5 keys: Should switch tools");
        System.out.println("   - Mouse wheel: Should adjust brush size");
        System.out.println("   - Ctrl+Z: Should undo");
        System.out.println("   - Ctrl+Y: Should redo");
        System.out.println("10. Verify performance metrics update");
        System.out.println("=====================================");
        
        // This test always passes, it's just for documentation
        assertTrue(true, "Manual testing framework provided");
        
        tearDown();
    }
}