package dev.marfien.extensions.environment;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractExtensionEnvironment implements ExtensionEnvironment {

    private final ExtensionEnvironmentModule module = new ExtensionEnvironmentModule();

    @Override
    public <T> AnnotatedBindingBuilder<T> bind(@NotNull Class<T> clazz) {
        return this.module.bind(clazz);
    }

    @Override
    public <T> AnnotatedBindingBuilder<T> bind(@NotNull TypeLiteral<T> typeLiteral) {
        return this.module.bind(typeLiteral);
    }

    @Override
    public <T> LinkedBindingBuilder<T> bind(@NotNull Key<T> key) {
        return this.module.bind(key);
    }

    @Override
    public Module getModule() {
        return this.module;
    }

    private static class ExtensionEnvironmentModule extends AbstractModule {

        @Override
        protected <T> LinkedBindingBuilder<T> bind(Key<T> key) {
            return super.bind(key);
        }

        @Override
        protected <T> AnnotatedBindingBuilder<T> bind(TypeLiteral<T> typeLiteral) {
            return super.bind(typeLiteral);
        }

        @Override
        protected <T> AnnotatedBindingBuilder<T> bind(Class<T> clazz) {
            return super.bind(clazz);
        }
    }

}
