package app.renderer.terrain.sculpting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Brush Settings Tests")
public class BrushSettingsTest {

    private BrushSettings brushSettings;

    @BeforeEach
    void setUp() {
        brushSettings = new BrushSettings();
    }

    @Test
    @DisplayName("Default values are correct")
    void testDefaultValues() {
        assertEquals(50.0f, brushSettings.getSize(), 0.01f, "Default size should be 50.0");
        assertEquals(0.5f, brushSettings.getStrength(), 0.01f, "Default strength should be 0.5");
        assertEquals(0.8f, brushSettings.getFalloff(), 0.01f, "Default falloff should be 0.8");
        assertEquals(0.0f, brushSettings.getTargetHeight(), 0.01f, "Default target height should be 0.0");
        assertEquals(BrushType.RAISE, brushSettings.getBrushType(), "Default brush type should be RAISE");
        assertEquals(BrushShape.CIRCLE, brushSettings.getShape(), "Default shape should be CIRCLE");
        assertEquals(FalloffType.SMOOTH, brushSettings.getFalloffType(), "Default falloff type should be SMOOTH");
        assertTrue(brushSettings.isShowPreview(), "Default show preview should be true");
    }

    @Test
    @DisplayName("Size bounds are enforced")
    void testSizeBounds() {
        // Test minimum bound
        brushSettings.setSize(-10.0f);
        assertEquals(1.0f, brushSettings.getSize(), 0.01f, "Size should be clamped to minimum 1.0");
        
        // Test maximum bound
        brushSettings.setSize(1000.0f);
        assertEquals(200.0f, brushSettings.getSize(), 0.01f, "Size should be clamped to maximum 200.0");
        
        // Test valid value
        brushSettings.setSize(75.0f);
        assertEquals(75.0f, brushSettings.getSize(), 0.01f, "Valid size should be set correctly");
    }

    @Test
    @DisplayName("Strength bounds are enforced")
    void testStrengthBounds() {
        // Test minimum bound
        brushSettings.setStrength(-1.0f);
        assertEquals(0.01f, brushSettings.getStrength(), 0.001f, "Strength should be clamped to minimum 0.01");
        
        // Test maximum bound
        brushSettings.setStrength(5.0f);
        assertEquals(2.0f, brushSettings.getStrength(), 0.01f, "Strength should be clamped to maximum 2.0");
        
        // Test valid value
        brushSettings.setStrength(1.0f);
        assertEquals(1.0f, brushSettings.getStrength(), 0.01f, "Valid strength should be set correctly");
    }

    @Test
    @DisplayName("Falloff bounds are enforced")
    void testFalloffBounds() {
        // Test minimum bound
        brushSettings.setFalloff(0.05f);
        assertEquals(0.1f, brushSettings.getFalloff(), 0.01f, "Falloff should be clamped to minimum 0.1");
        
        // Test maximum bound
        brushSettings.setFalloff(2.0f);
        assertEquals(1.0f, brushSettings.getFalloff(), 0.01f, "Falloff should be clamped to maximum 1.0");
        
        // Test valid value
        brushSettings.setFalloff(0.6f);
        assertEquals(0.6f, brushSettings.getFalloff(), 0.01f, "Valid falloff should be set correctly");
    }

    @Test
    @DisplayName("Falloff calculation works correctly for different types")
    void testFalloffCalculation() {
        float distance = 25.0f; // Half radius
        float radius = 50.0f;
        
        // The actual calculateFalloff method takes distance normalized by radius (0-1)
        float normalizedDistance = distance / radius;
        
        // Test linear falloff
        brushSettings.setFalloffType(FalloffType.LINEAR);
        float linearResult = brushSettings.calculateFalloff(normalizedDistance);
        assertEquals(0.5f, linearResult, 0.01f, "Linear falloff should be 0.5 at half radius");
        
        // Test smooth falloff (should be different from linear)
        brushSettings.setFalloffType(FalloffType.SMOOTH);
        float smoothResult = brushSettings.calculateFalloff(normalizedDistance);
        assertNotEquals(linearResult, smoothResult, "Smooth falloff should differ from linear");
        assertTrue(smoothResult >= 0.0f && smoothResult <= 1.0f, "Falloff should be in valid range");
        
        // Test gaussian falloff
        brushSettings.setFalloffType(FalloffType.GAUSSIAN);
        float gaussianResult = brushSettings.calculateFalloff(normalizedDistance);
        assertTrue(gaussianResult >= 0.0f && gaussianResult <= 1.0f, "Gaussian falloff should be in valid range");
        
        // Test polynomial falloff
        brushSettings.setFalloffType(FalloffType.POLYNOMIAL);
        float polyResult = brushSettings.calculateFalloff(normalizedDistance);
        assertTrue(polyResult >= 0.0f && polyResult <= 1.0f, "Polynomial falloff should be in valid range");
    }

    @Test
    @DisplayName("Falloff calculation handles edge cases")
    void testFalloffEdgeCases() {
        float radius = 50.0f;
        
        // Test at center (normalized distance = 0)
        float centerResult = brushSettings.calculateFalloff(0.0f);
        assertEquals(1.0f, centerResult, 0.01f, "Falloff at center should be 1.0");
        
        // Test at edge (normalized distance = 1.0)
        float edgeResult = brushSettings.calculateFalloff(1.0f);
        assertEquals(0.0f, edgeResult, 0.01f, "Falloff at edge should be 0.0");
        
        // Test outside brush (normalized distance > 1.0)
        float outsideResult = brushSettings.calculateFalloff(1.5f);
        assertEquals(0.0f, outsideResult, 0.01f, "Falloff outside brush should be 0.0");
    }

    @Test
    @DisplayName("Brush type enum has correct values")
    void testBrushTypeEnum() {
        assertEquals("🔺", BrushType.RAISE.getIcon(), "RAISE icon should be correct");
        assertEquals("🔻", BrushType.LOWER.getIcon(), "LOWER icon should be correct");
        assertEquals("🌊", BrushType.SMOOTH.getIcon(), "SMOOTH icon should be correct");
        assertEquals("📏", BrushType.FLATTEN.getIcon(), "FLATTEN icon should be correct");
        assertEquals("🎲", BrushType.NOISE.getIcon(), "NOISE icon should be correct");

        assertEquals("Raise", BrushType.RAISE.getDisplayName(), "RAISE display name should be correct");
        assertEquals("Lower", BrushType.LOWER.getDisplayName(), "LOWER display name should be correct");
        assertEquals("Smooth", BrushType.SMOOTH.getDisplayName(), "SMOOTH display name should be correct");
        assertEquals("Flatten", BrushType.FLATTEN.getDisplayName(), "FLATTEN display name should be correct");
        assertEquals("Noise", BrushType.NOISE.getDisplayName(), "NOISE display name should be correct");
    }
}