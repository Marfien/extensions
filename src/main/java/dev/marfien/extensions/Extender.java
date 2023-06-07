package dev.marfien.extensions;

import com.google.common.collect.Maps;
import dev.marfien.extensions.environment.ExtensionEnvironment;
import dev.marfien.extensions.extension.DiscoveredExtension;
import dev.marfien.extensions.extension.ExtensionDescription;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class Extender {

    private final ExtensionEnvironment environment;

    private final Discoverer discoverer = new Discoverer(this);

    private final Map<String, DiscoveredExtension> extensionById = Maps.newConcurrentMap();
    private final Map<Object, DiscoveredExtension> extensionsByInstance = Maps.newConcurrentMap();

    public void initializeSingle(@NotNull DiscoveredExtension extension) {
        ExtensionDescription description = extension.getDescription();
        String entrypoint = description.entrypoint();

        this.checkDependenciesLoaded(description);
    }

    private boolean checkDependenciesDiscovered(final ExtensionDescription description) {
        for (ExtensionDescription.Dependency dependency : description.dependencies()) {
            // irrelevant for just checking if the extension is eligible to be initialized
            if (dependency.soft()) continue;

            if (!this.extensionById.containsKey(dependency.id()))
                return false;
        }

        return true;
    }

    private boolean checkDependenciesLoaded(final ExtensionDescription description) {
        for (ExtensionDescription.Dependency dependency : description.dependencies()) {
            // If the dependency is required we need to check if it is initialized.
            if (!dependency.soft()) {
                if (!this.isInitialized(dependency.id())) return false;
                continue;
            }

            // This is the harder part.
            // Therefore, that it is optional, we cannot just use this.isInitialized() cause
            // that would fail as well when the dependency is not present at all.
            // But we want it to be initialized when it is present but don't really care if it is not
            // there at all.
            Optional<DiscoveredExtension> optionalDiscoveredDependency = this.findById(dependency.id());

            if (optionalDiscoveredDependency.isEmpty()) continue;

            DiscoveredExtension discoveredDependency = optionalDiscoveredDependency.get();

            if (discoveredDependency.getInstance().isEmpty()) return false;

        }

        return true;
    }

    public Optional<DiscoveredExtension> findByInstance(@NotNull Object instance) {
        return Optional.ofNullable(this.extensionsByInstance.get(instance));
    }

    public boolean isKnown(@NotNull String id) {
        return this.extensionById.containsKey(id);
    }

    public boolean isInitialized(@NotNull String id) {
        return this.findById(id)
                .flatMap(DiscoveredExtension::getInstance)
                .isPresent();
    }

    public Optional<DiscoveredExtension> findById(@NotNull String id) {
        return Optional.ofNullable(this.extensionById.get(id));
    }

    public Collection<DiscoveredExtension> findAll() {
        assert this.extensionsByInstance.size() == this.extensionById.size();
        return List.copyOf(this.extensionsByInstance.values());
    }

    public ExtensionEnvironment getEnvironment() {
        return this.environment;
    }

    public Discoverer getDiscoverer() {
        return this.discoverer;
    }
}
