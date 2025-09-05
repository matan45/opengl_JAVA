package app.renderer.terrain.sculpting;

import app.ecs.Entity;
import app.ecs.components.TerrainSculptingComponent;
import app.math.components.OLTransform;
import app.math.OLVector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Terrain Sculpting Component Tests")
public class TerrainSculptingComponentTest {

    private Entity testEntity;
    private TerrainSculptingComponent sculptingComponent;

    @BeforeEach
    void setUp() {
        testEntity = new Entity("TestEntity", new OLTransform());
        sculptingComponent = new TerrainSculptingComponent(testEntity);
    }

    @Test
    @DisplayName("Component initializes with correct defaults")
    void testComponentInitialization() {
        assertFalse(sculptingComponent.isActive(), "Component should be inactive by default");
        assertFalse(sculptingComponent.isCurrentlySculpting(), "Should not be sculpting by default");
        assertEquals(0, sculptingComponent.getModificationsCount(), "Modifications count should be zero");
        
        assertNotNull(sculptingComponent.getBrushSettings(), "Brush settings should not be null");
        assertNotNull(sculptingComponent.getLastSculptPosition(), "Last sculpt position should not be null");
        
        // Owner entity relationship is maintained internally by the component system
        assertTrue(testEntity.hasComponent(TerrainSculptingComponent.class), "Entity should have the sculpting component");
    }

    @Test
    @DisplayName("Active state can be toggled")
    void testActiveStateToggle() {
        // Initially false
        assertFalse(sculptingComponent.isActive());
        
        // Set to true
        sculptingComponent.setActive(true);
        assertTrue(sculptingComponent.isActive());
        
        // Set back to false
        sculptingComponent.setActive(false);
        assertFalse(sculptingComponent.isActive());
    }

    @Test
    @DisplayName("Sculpting state can be toggled")
    void testSculptingStateToggle() {
        // Initially false
        assertFalse(sculptingComponent.isCurrentlySculpting());
        
        // Set to true
        sculptingComponent.setCurrentlySculpting(true);
        assertTrue(sculptingComponent.isCurrentlySculpting());
        
        // Set back to false
        sculptingComponent.setCurrentlySculpting(false);
        assertFalse(sculptingComponent.isCurrentlySculpting());
    }

    @Test
    @DisplayName("Modifications count can be incremented and reset")
    void testModificationsCount() {
        // Initially zero
        assertEquals(0, sculptingComponent.getModificationsCount());
        
        // Increment
        sculptingComponent.incrementModifications();
        assertEquals(1, sculptingComponent.getModificationsCount());
        
        // Increment again
        sculptingComponent.incrementModifications();
        assertEquals(2, sculptingComponent.getModificationsCount());
        
        // Reset
        sculptingComponent.resetModificationsCount();
        assertEquals(0, sculptingComponent.getModificationsCount());
    }

    @Test
    @DisplayName("Last sculpt position can be updated")
    void testLastSculptPosition() {
        OLVector3f newPosition = new OLVector3f(10.0f, 5.0f, 15.0f);
        sculptingComponent.setLastSculptPosition(newPosition);
        
        OLVector3f retrievedPosition = sculptingComponent.getLastSculptPosition();
        assertEquals(10.0f, retrievedPosition.x, 0.01f, "X position should match");
        assertEquals(5.0f, retrievedPosition.y, 0.01f, "Y position should match");
        assertEquals(15.0f, retrievedPosition.z, 0.01f, "Z position should match");
    }

    @Test
    @DisplayName("Brush settings reference is maintained")
    void testBrushSettingsReference() {
        BrushSettings originalBrush = sculptingComponent.getBrushSettings();
        
        // Modify brush settings
        originalBrush.setSize(75.0f);
        originalBrush.setBrushType(BrushType.SMOOTH);
        
        // Check that changes persist
        BrushSettings retrievedBrush = sculptingComponent.getBrushSettings();
        assertEquals(75.0f, retrievedBrush.getSize(), 0.01f, "Size change should persist");
        assertEquals(BrushType.SMOOTH, retrievedBrush.getBrushType(), "Brush type change should persist");
        
        // Should be the same object reference
        assertSame(originalBrush, retrievedBrush, "Should return the same brush settings object");
    }

    @Test
    @DisplayName("Component cleanup works correctly")
    void testCleanup() {
        // Set some state
        sculptingComponent.setActive(true);
        sculptingComponent.setCurrentlySculpting(true);
        sculptingComponent.incrementModifications();
        
        // Cleanup
        sculptingComponent.cleanUp();
        
        // State should be reset
        assertFalse(sculptingComponent.isActive(), "Should be inactive after cleanup");
        assertFalse(sculptingComponent.isCurrentlySculpting(), "Should not be sculpting after cleanup");
        // Note: modifications count might or might not be reset depending on implementation
    }

    @Test
    @DisplayName("ImGui rendering doesn't crash without OpenGL context")
    void testImguiRenderingSafety() {
        // This test ensures that calling render doesn't crash even without OpenGL context
        // In a real environment with OpenGL context, this would render the UI
        assertDoesNotThrow(() -> {
            sculptingComponent.imguiDraw(); // ImGui rendering method
        }, "ImGui rendering should not throw without OpenGL context");
    }

    @Test
    @DisplayName("Component handles null position gracefully")
    void testNullPositionHandling() {
        assertDoesNotThrow(() -> {
            sculptingComponent.setLastSculptPosition(null);
        }, "Setting null position should not throw");
        
        // The implementation should handle null gracefully or provide a default
        // The component should handle null positions gracefully
        // We can't easily test the internal null handling without implementation details
        assertDoesNotThrow(() -> {
            OLVector3f position = sculptingComponent.getLastSculptPosition();
        }, "Getting position after setting null should not crash");
    }
}