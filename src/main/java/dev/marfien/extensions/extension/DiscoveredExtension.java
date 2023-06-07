package dev.marfien.extensions.extension;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Optional;

public final class DiscoveredExtension {

    private Object instance;
    private ExtensionClassLoader classLoader;

    private final Path file;
    private final ExtensionDescription description;

    @ApiStatus.Internal
    public DiscoveredExtension(@NotNull Path file, @NotNull ExtensionDescription description) {
        this.file = file;
        this.description = description;
    }

    @ApiStatus.Internal
    public void setInstance(@NotNull Object object) {
        if (this.instance != null)
            throw new IllegalStateException("The extension '%s' is already initialized.".formatted(this.description.id()));

        this.instance = object;
    }

    @ApiStatus.Internal
    public void setClassLoader(@NotNull ExtensionClassLoader classLoader) {
        if (this.instance != null)
            throw new IllegalStateException("The extension '%s' already has a class loader.".formatted(this.description.id()));

        this.classLoader = classLoader;
    }

    public Optional<Object> getInstance() {
        return Optional.ofNullable(this.instance);
    }

    public ExtensionClassLoader getClassLoader() {
        return this.classLoader;
    }

    public Path getFile() {
        return this.file;
    }

    public ExtensionDescription getDescription() {
        return this.description;
    }
}
