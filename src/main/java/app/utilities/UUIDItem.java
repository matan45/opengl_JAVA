package app.utilities;

import java.util.random.RandomGenerator;

public class UUIDItem {
    private UUIDItem() {
    }

    private static final RandomGenerator generator = RandomGenerator.of("L64X256MixRandom");

    public static long generateUUID() {
        return generator.nextLong(1, Long.MAX_VALUE - 1);
    }
}
