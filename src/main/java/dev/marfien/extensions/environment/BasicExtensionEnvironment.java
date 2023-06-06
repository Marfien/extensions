package dev.marfien.extensions.environment;

import dev.marfien.extensions.annotation.inject.RandomValue;
import org.jetbrains.annotations.NotNull;

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

    @Override
    public Path getDefaultExtensionsContainer() {
        return this.extensionsContainer;
    }

    @Override
    public Path getDefaultLibrariesContainer() {
        return this.librariesContainer;
    }

    @Override
    public void configure() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        bind(Boolean.TYPE)  .annotatedWith(RandomValue.class).toProvider(random::nextBoolean);
        bind(Integer.TYPE)  .annotatedWith(RandomValue.class).toProvider(random::nextInt);
        bind(Double.TYPE)   .annotatedWith(RandomValue.class).toProvider(random::nextDouble);
        bind(Float.TYPE)    .annotatedWith(RandomValue.class).toProvider(random::nextFloat);
        bind(Short.TYPE)    .annotatedWith(RandomValue.class).toProvider(() -> (short) random.nextInt(Short.MAX_VALUE));
        bind(Byte.TYPE)     .annotatedWith(RandomValue.class).toProvider(() -> (byte) random.nextInt(Byte.MAX_VALUE));
        bind(UUID.class)    .annotatedWith(RandomValue.class).toProvider(() -> new UUID(random.nextLong(), random.nextLong()));

    }

}
