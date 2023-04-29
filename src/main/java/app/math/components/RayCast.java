package app.math.components;

import app.math.OLVector2f;
import app.math.OLVector3f;
import app.math.OLVector4f;
import app.math.OLMatrix4f;

public class RayCast {
    public static Camera camera;

    public static OLVector3f calculateMouseRay() {
        OLVector2f normalizedCoords = MouseCursor.getNormalizePosition();
        OLVector4f clipCoords = new OLVector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
        OLVector4f eyeCoords = toEyeCoords(clipCoords);
        OLVector3f worldRay = toWorldCoords(eyeCoords);
        return worldRay;
    }

    private static OLVector3f toWorldCoords(OLVector4f eyeCoords) {
        OLMatrix4f invertedView = OLMatrix4f.invert(viewMatrix, null);
        OLVector4f rayWorld = OLMatrix4f.transform(invertedView, eyeCoords, null);
        OLVector3f mouseRay = new OLVector3f(rayWorld.x, rayWorld.y, rayWorld.z);
        mouseRay.normalise();
        return mouseRay;
    }

    private static OLVector4f toEyeCoords(OLVector4f clipCoords) {
        OLMatrix4f invertedProjection = OLMatrix4f.invert(projectionMatrix, null);
        OLVector4f eyeCoords = OLMatrix4f.transform(invertedProjection, clipCoords, null);
        return new OLVector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
    }
}
