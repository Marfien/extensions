package dev.marfien.extensions.extension;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Optional;

public final class DiscoveredExtension {

    private Object extensionObject;

    private final Path file;
    private final ExtensionDescription description;

    @ApiStatus.Internal
    public DiscoveredExtension(@NotNull Path file, @NotNull ExtensionDescription description) {
        this.file = file;
        this.description = description;
    }

    public Optional<Object> getExtensionObject() {
        return Optional.ofNullable(this.extensionObject);
    }

    public Path getFile() {
        return this.file;
    }

    public ExtensionDescription getDescription() {
        return this.description;
    }
}
