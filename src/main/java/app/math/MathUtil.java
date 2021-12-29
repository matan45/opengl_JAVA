package app.math;

public class MathUtil {

    public static float invsqrt(float x) {
        float xhalf = 0.5f * x;
        int i = Float.floatToIntBits(x);
        i = 0x5f3759df - (i >> 1);
        x = Float.intBitsToFloat(i);
        x = x * (1.5f - (xhalf * x * x));
        return x;
    }

    public float clamp(float value, float min, float max) {
        return (value < min) ? min : Math.min(value, max);
    }
}
