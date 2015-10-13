package org.mongodb.morphia.utils;

import java.util.Optional;
import java.util.function.BooleanSupplier;

public class Factories {
    public static <T> Optional<T> getOptionalBasedOnCondition(BooleanSupplier condition, T nullableValue) {
        if (condition.getAsBoolean()) {
            return Optional.ofNullable(nullableValue);
        } else {
            return Optional.empty();
        }
    }
}
