package app.math;

public class MathUtil {
    public static final float epsilon = 1.e-8f;

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
        final float DeltaMove = dist * clamp(deltaTime * interpSpeed, 0.f, 1.f);

        return current + DeltaMove;
    }


}
