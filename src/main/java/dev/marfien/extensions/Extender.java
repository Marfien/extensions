package dev.marfien.extensions;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import dev.marfien.extensions.environment.ExtensionEnvironment;
import dev.marfien.extensions.extension.DiscoveredExtension;
import dev.marfien.extensions.extension.ExtensionClassLoader;
import dev.marfien.extensions.extension.ExtensionDescription;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;

public final class Extender {

    private final ExtensionEnvironment environment;

    private final Discoverer discoverer = new Discoverer(this);
    private final DiscoveredExtensionModule module = this.new DiscoveredExtensionModule();

    private final Map<String, DiscoveredExtension> extensionById = Maps.newConcurrentMap();
    private final Map<Object, DiscoveredExtension> extensionsByInstance = Maps.newConcurrentMap();

    public Extender(@NotNull ExtensionEnvironment environment) {
        this.environment = environment;
    }

    public void initialize() {
        Collection<DiscoveredExtension> extensions = new ArrayList<>(this.findAll());

        // No need to remove nothing
        if (!this.extensionsByInstance.isEmpty())
            // but we don't want to instantiate an extension twice
            extensions.removeAll(this.findAllInitialized());

        // check for all dependencies
        for (DiscoveredExtension extension : extensions) {
            Optional<String> optionalMissing = this.checkDependenciesDiscovered(extension.getDescription());

            // throw an exception if a dependency is missing
            if (optionalMissing.isPresent())
                throw new RuntimeException(); // TODO Missing dependency exception
        }

        // build the dependency graph to get the initialization order
        DependencyGraph graph = new DependencyGraph(extensions);
        List<DiscoveredExtension> orderedExtensions = graph.sort();

        this.instantiate(orderedExtensions);
    }

    private void instantiate(@NotNull List<DiscoveredExtension> extensions) {
        Injector injector = Guice.createInjector(this.environment.getModule(), this.module);

        for (DiscoveredExtension extension : extensions) {
            ExtensionClassLoader classLoader = this.createClassLoader(extension);
            try {
                // instantiate with guice
                Class<?> main = classLoader.loadClass(extension.getDescription().entrypoint());
                Object instance = injector.getInstance(main);

                // Set instances on the wrapped extension class
                extension.setClassLoader(classLoader);
                extension.setInstance(instance);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e); // TODO custom exception
            }

        }
    }

    private ExtensionClassLoader createClassLoader(@NotNull DiscoveredExtension extension) {
        ExtensionDescription description = extension.getDescription();
        Collection<ExtensionDescription.Dependency> dependencies = description.dependencies();
        Collection<ExtensionClassLoader> classLoaders = Lists.newArrayList();

        // Add all dependencies to the class path
        // An extension will not have access to dependencies it has not declared
        for (ExtensionDescription.Dependency dependency : dependencies) {
            Optional<DiscoveredExtension> optionalDiscoveredExtension = this.findById(dependency.id());

            // only add if the class loader is present
            // cannot add class loaders of soft dependencies that are not present
            optionalDiscoveredExtension
                    .map(DiscoveredExtension::getClassLoader)
                    .ifPresent(classLoaders::add);
        }

        return new ExtensionClassLoader("ExtensionClassLoader#%s".formatted(description.id()), classLoaders, Extender.class.getClassLoader());
    }

    /**
     * Checks if all hard dependencies of an extension are discovered
     *
     * @param description the description of the extension to check
     * @return An Optional may contain the id of the missing extension.
     */
    private Optional<String> checkDependenciesDiscovered(@NotNull ExtensionDescription description) {
        for (ExtensionDescription.Dependency dependency : description.dependencies()) {
            // irrelevant for just checking if the extension is eligible to be initialized
            if (dependency.soft()) continue;

            // but if it is a required dependency it must be present
            String dependencyId = dependency.id();
            if (!this.extensionById.containsKey(dependencyId))
                return Optional.of(dependencyId);
        }

        return Optional.empty();
    }

    /**
     * Checks if the dependency is present and initialized.
     * A soft dependency is ether present and initialized or not known at all.
     *
     * @param description the description of the extension to check
     * @return true if all dependencies are initialized correctly.
     */
    private boolean checkDependenciesInitialized(@NotNull ExtensionDescription description) {
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

    private class DiscoveredExtensionModule extends AbstractModule {

        @Override
        protected void configure() {
            Extender.this.extensionById.forEach((id, extension) -> {
                this.bind(DiscoveredExtension.class)
                        .annotatedWith(Names.named(id))
                        .toInstance(extension);
                this.bind(ExtensionDescription.class)
                        .annotatedWith(Names.named(id))
                        .toInstance(extension.getDescription());
            });
        }
    }

}
