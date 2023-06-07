package dev.marfien.extensions;

import com.google.common.collect.Maps;
import dev.marfien.extensions.environment.ExtensionEnvironment;
import dev.marfien.extensions.extension.DiscoveredExtension;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public final class Extender {

    private final ExtensionEnvironment environment;

    private final Discoverer discoverer = new Discoverer(this);

    private final Map<String, DiscoveredExtension> extensionById = Maps.newConcurrentMap();
    private final Map<Object, DiscoveredExtension> extensionsByInstance = Maps.newConcurrentMap();

    public Optional<DiscoveredExtension> findByInstance(@NotNull Object instance) {
        return Optional.ofNullable(this.extensionsByInstance.get(instance));
    }

    public boolean isKnown(@NotNull String id) {
        return this.extensionById.containsKey(id);
    }

    public Optional<DiscoveredExtension> findById(@NotNull String id) {
        return Optional.ofNullable(this.extensionById.get(id));
    }

    public Collection<DiscoveredExtension> findAll() {
        assert this.extensionsByInstance.size() == this.extensionById.size();
        return this.extensionsByInstance.values();
    }

    public ExtensionEnvironment getEnvironment() {
        return this.environment;
    }

    public Discoverer getDiscoverer() {
        return this.discoverer;
    }
}
