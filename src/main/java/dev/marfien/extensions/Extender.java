package dev.marfien.extensions;

import com.google.common.collect.Maps;
import dev.marfien.extensions.environment.ExtensionEnvironment;
import dev.marfien.extensions.extension.DiscoveredExtension;
import dev.marfien.extensions.extension.ExtensionDescription;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Extender {

    private final ExtensionEnvironment environment;

    private final Discoverer discoverer = new Discoverer(this);

    private final Map<String, DiscoveredExtension> extensionById = Maps.newConcurrentMap();
    private final Map<Object, DiscoveredExtension> extensionsByInstance = Maps.newConcurrentMap();

    public Extender(@NotNull ExtensionEnvironment environment) {
        this.environment = environment;
    }

    public void initialize() {
        Collection<DiscoveredExtension> extensions = this.findAll();

        // No need to remove nothing
        if (!this.extensionsByInstance.isEmpty())
            // but we don't want to instantiate an extension twice
            extensions.removeAll(this.findAllInitialized());


    }

    private boolean checkDependenciesDiscovered(@NotNull ExtensionDescription description) {
        for (ExtensionDescription.Dependency dependency : description.dependencies()) {
            // irrelevant for just checking if the extension is eligible to be initialized
            if (dependency.soft()) continue;

            // but if it is a required dependency it must be present
            if (!this.extensionById.containsKey(dependency.id()))
                return false;
        }

        return true;
    }

    private boolean checkDependenciesLoaded(@NotNull ExtensionDescription description) {
        for (ExtensionDescription.Dependency dependency : description.dependencies()) {
            // If the dependency is required we need to check if it is initialized.
            if (!dependency.soft()) {
                if (!this.isInitialized(dependency.id())) return false;
                continue;
            }

            // Fist try to find the dependency
            Optional<DiscoveredExtension> optionalDiscoveredDependency = this.findById(dependency.id());

            // if it is not known at all we can simply ignore it, because it is optional
            if (optionalDiscoveredDependency.isEmpty()) continue;

            // but if it is found we need to check if it is instantiated
            DiscoveredExtension discoveredDependency = optionalDiscoveredDependency.get();

            // if the instance of the dependency is not available it is not instantiated
            // Therefore, it is known but need to be instantiated first
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
        return List.copyOf(this.extensionById.values());
    }

    public Collection<DiscoveredExtension> findAllInitialized() {
        return List.copyOf(this.extensionsByInstance.values());
    }

    public ExtensionEnvironment getEnvironment() {
        return this.environment;
    }

    public Discoverer getDiscoverer() {
        return this.discoverer;
    }

}
