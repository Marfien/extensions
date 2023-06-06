package dev.marfien.extensions;

import dev.marfien.extensions.environment.ExtensionEnvironment;
import org.jetbrains.annotations.NotNull;

public class Extensions {

    static {
        System.setProperty(PropertyKey.RANDOM_STRING_LENGTH, "16");
    }

    public static Extender create(@NotNull ExtensionEnvironment environment) {
        return null; // TODO implement
    }

}