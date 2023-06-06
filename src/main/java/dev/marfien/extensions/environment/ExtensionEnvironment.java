package dev.marfien.extensions.environment;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public interface ExtensionEnvironment {

    Path getDefaultExtensionsContainer();

    Path getDefaultLibrariesContainer();

    void configure();

    <T> AnnotatedBindingBuilder<T> bind(@NotNull Class<T> clazz);

    <T> AnnotatedBindingBuilder<T> bind(@NotNull TypeLiteral<T> typeLiteral);

    <T> LinkedBindingBuilder<T> bind(@NotNull Key<T> key);

}
