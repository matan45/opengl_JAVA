package app.math.components;

import app.math.OLMatrix4f;
import app.math.OLVector2f;
import app.math.OLVector3f;
import app.math.OLVector4f;
import imgui.ImGui;

public class RayCast {
    public static Camera camera;

    public static OLVector3f calculateMouseRay() {
        OLVector2f normalizedCords = new OLVector2f(ImGui.getMousePos().x,ImGui.getMousePos().y).normalize();
        OLVector4f clipCords = new OLVector4f(normalizedCords.x, normalizedCords.y, -1.0f, 1.0f);
        OLVector4f eyeCords = toEyeCords(clipCords);
        return toWorldCords(eyeCords);
    }

    private static OLVector3f toWorldCords(OLVector4f eyeCords) {
        OLMatrix4f invertedView = camera.getViewMatrix().invert();
        OLVector4f rayWorld = invertedView.transform(eyeCords);
        return new OLVector3f(rayWorld.x, rayWorld.y, rayWorld.z).normalize();
    }

    private static OLVector4f toEyeCords(OLVector4f clipCords) {
        OLMatrix4f invertedProjection = camera.getProjectionMatrix().invert();
        OLVector4f eyeCords = invertedProjection.transform(clipCords);
        return new OLVector4f(eyeCords.x, eyeCords.y, eyeCords.z, 0f);
    }
}
