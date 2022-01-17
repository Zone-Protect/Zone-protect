package utils;

import org.junit.jupiter.api.Assertions;

import java.util.Collection;

public final class CollectionAssert {

    private CollectionAssert() {
    }

    public static <T> void collectionEquals(Collection<? extends T> expected, Collection<T> value) {
        if (expected.size() != value.size()) {
            Assertions.fail("Found more in value then expected. Expected: " +
                    expected +
                    " Value: " +
                    value);
            return;
        }
        for (T exp : expected) {
            if (!value.contains(exp)) {
                Assertions.fail("Expected " +
                        exp.toString() +
                        " however failed to find it in: " +
                        value);
            }
        }
    }
}
