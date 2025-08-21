package app.math;

public class MathUtil {
    private MathUtil() {
    }

    public static final float epsilon = 1.e-8f;

    public static float getRandomNumber(float min, float max) {
        return (float) ((Math.random() * (max - min)) + min);
    }

    public static OLVector3f getRandomNumber(OLVector3f min, OLVector3f max) {
        return new OLVector3f((float) ((Math.random() * (max.x - min.x)) + min.x),
                (float) ((Math.random() * (max.y - min.y)) + min.y),
                (float) ((Math.random() * (max.z - min.z)) + min.z));

    }

    public static float barryCentric(OLVector3f p1, OLVector3f p2, OLVector3f p3, OLVector2f pos) {
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }

    public static float inverseSqrt(float r) {
        return 1.0f / (float) java.lang.Math.sqrt(r);
    }

    public static float clamp(float value, float min, float max) {
        return (value < min) ? min : Math.min(value, max);
    }

    public static float interpTo(float current, float target, float deltaTime, float interpSpeed) {
        // If no interp speed, jump to target value
        if (interpSpeed <= 0.f) {
            return target;
        }

        // Distance to reach
        final float dist = target - current;

        // If distance is too small, just set the desired location
        if (Math.sqrt(dist) < epsilon) {
            return target;
        }

        // Delta Move, Clamp so we do not over shoot.
        final float deltaMove = dist * clamp(deltaTime * interpSpeed, 0.f, 1.f);

        return current + deltaMove;
    }


}
