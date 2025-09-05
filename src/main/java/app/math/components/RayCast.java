package app.math.components;

import app.math.OLMatrix4f;
import app.math.OLVector3f;
import app.math.OLVector4f;
import imgui.ImGui;

public class RayCast {
    public static Camera camera;
    private static final float RAY_RANGE = 200.0f;

    public static OLVector3f calculateMouseRay(float width, float height) {
        float x = (float) (-1.0 + 2.0 * ImGui.getMousePos().x / width);
        float y = (float) (1.0 - 2.0 * ImGui.getMousePos().y / height);
        OLVector4f clipCords = new OLVector4f(x, y, -1.0f, 1.0f);
        OLVector4f eyeCords = toEyeCords(clipCords);
        return toWorldCords(eyeCords).normalize();
    }
    
    public static OLVector3f calculateMouseRayOrigin(float width, float height) {
        float x = (float) (-1.0 + 2.0 * ImGui.getMousePos().x / width);
        float y = (float) (1.0 - 2.0 * ImGui.getMousePos().y / height);
        
        // Calculate near plane position (z = -1 in clip space)
        OLVector4f nearClipCoords = new OLVector4f(x, y, -1.0f, 1.0f);
        OLVector4f nearEyeCoords = toEyeCordsForOrigin(nearClipCoords);
        return toWorldCords(nearEyeCoords);
    }
    
    private static OLVector4f toEyeCordsForOrigin(OLVector4f clipCords) {
        OLMatrix4f invertedProjection = camera.getProjectionMatrix().invert();
        OLVector4f eyeCords = invertedProjection.transform(clipCords);
        // For origin calculation, we need the actual position, not direction
        if (eyeCords.w != 0.0f) {
            eyeCords.x /= eyeCords.w;
            eyeCords.y /= eyeCords.w;
            eyeCords.z /= eyeCords.w;
        }
        return new OLVector4f(eyeCords.x, eyeCords.y, eyeCords.z, 1.0f);
    }

    private static OLVector3f toWorldCords(OLVector4f eyeCords) {
        OLMatrix4f invertedView = camera.getViewMatrix().invert();
        OLVector4f rayWorld = invertedView.transform(eyeCords);
        return new OLVector3f(rayWorld.x, rayWorld.y, rayWorld.z);
    }

    private static OLVector4f toEyeCords(OLVector4f clipCords) {
        OLMatrix4f invertedProjection = camera.getProjectionMatrix().invert();
        OLVector4f eyeCords = invertedProjection.transform(clipCords);
        return new OLVector4f(eyeCords.x, eyeCords.y, -1.0f, 0f);
    }
}
