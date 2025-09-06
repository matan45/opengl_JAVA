package app.utilities.debug;

public class TerrainDebug {
    private static boolean debugEnabled = true;
    
    public static void setDebugEnabled(boolean enabled) {
        debugEnabled = enabled;
        if (enabled) {
            System.out.println("🔍 [TERRAIN_DEBUG] Debug mode ENABLED");
        } else {
            System.out.println("🔍 [TERRAIN_DEBUG] Debug mode DISABLED");
        }
    }
    
    public static void toggleDebug() {
        setDebugEnabled(!debugEnabled);
    }
    
    public static boolean isDebugEnabled() {
        return debugEnabled;
    }
    
    public static void println(String message) {
        if (debugEnabled) {
            System.out.println("🔍 [TERRAIN_DEBUG] " + message);
        }
    }
    
    public static void printf(String format, Object... args) {
        if (debugEnabled) {
            System.out.printf("🔍 [TERRAIN_DEBUG] " + format + "\n", args);
        }
    }
    
    public static void printSeparator(String title) {
        if (debugEnabled) {
            System.out.println("🔍 ═══════════════════════════════════════");
            System.out.println("🔍 " + title);
            System.out.println("🔍 ═══════════════════════════════════════");
        }
    }
    
    public static void printStatus() {
        System.out.println("🔍 [TERRAIN_DEBUG] Status: " + (debugEnabled ? "ENABLED" : "DISABLED"));
    }
}