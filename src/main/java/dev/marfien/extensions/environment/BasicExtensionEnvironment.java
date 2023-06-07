package dev.marfien.extensions.environment;

import dev.marfien.extensions.annotation.binding.ApplicationWorkingDirectory;
import dev.marfien.extensions.annotation.binding.RandomStringValue;
import dev.marfien.extensions.annotation.binding.RandomValue;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class BasicExtensionEnvironment extends AbstractExtensionEnvironment {

    private static final Path WORKING_DIR = Paths.get("").toAbsolutePath();

    private final Path extensionsContainer;
    private final Path librariesContainer;

    public BasicExtensionEnvironment(@NotNull String extensionsContainerName, @NotNull String librariesContainerName) {
        this.extensionsContainer = WORKING_DIR.resolve(extensionsContainerName);
        this.librariesContainer = WORKING_DIR.resolve(librariesContainerName);
    }

    public BasicExtensionEnvironment() {
        this("extensions", "libraries");
    }

    @Override
    public Path getExtensionsContainer() {
        return this.extensionsContainer;
    }

    @Override
    public Path getLibrariesContainer() {
        return this.librariesContainer;
    }

    @Override
    public void configure() {
        configureRandomValueBindings();
        configureRandomStringValueBindings();
        configureApplicationWorkingDirectoryBindings();
    }

    protected void configureApplicationWorkingDirectoryBindings() {
        bind(Path.class).annotatedWith(ApplicationWorkingDirectory.class).toInstance(WORKING_DIR);
        bind(File.class).annotatedWith(ApplicationWorkingDirectory.class).toProvider(WORKING_DIR::toFile);
    }

    protected void configureRandomStringValueBindings() {
        RandomStringValue.Template.injectBindings(super::bind);
    }

    protected void configureRandomValueBindings() {
        Random random = ThreadLocalRandom.current();
        bind(Boolean.TYPE)  .annotatedWith(RandomValue.class).toProvider(random::nextBoolean);
        bind(Integer.TYPE)  .annotatedWith(RandomValue.class).toProvider(random::nextInt);
        bind(Double.TYPE)   .annotatedWith(RandomValue.class).toProvider(random::nextDouble);
        bind(Float.TYPE)    .annotatedWith(RandomValue.class).toProvider(random::nextFloat);
        bind(Short.TYPE)    .annotatedWith(RandomValue.class).toProvider(() -> (short) random.nextInt(Short.MAX_VALUE));
        bind(Byte.TYPE)     .annotatedWith(RandomValue.class).toProvider(() -> (byte) random.nextInt(Byte.MAX_VALUE));
        bind(UUID.class)    .annotatedWith(RandomValue.class).toProvider(() -> new UUID(random.nextLong(), random.nextLong()));
    }

}
