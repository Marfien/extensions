package dev.marfien.extensions;

import com.google.common.collect.Lists;
import dev.marfien.extensions.environment.ExtensionEnvironment;
import dev.marfien.extensions.extension.DiscoveredExtension;
import dev.marfien.extensions.extension.ExtensionDescription;
import org.jetbrains.annotations.NotNull;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Discoverer {

    private static final Collection<String> ALLOWED_FILE_ENDINGS = List.of(".jar");

    private static boolean hasSupportedFileEnding(@NotNull Path path) {
        for (String allowedFileEnding : ALLOWED_FILE_ENDINGS) {
            if (path.endsWith(allowedFileEnding))
                return true;
        }

        return false;
    }

    private final Extender extender;
    private final ExtensionEnvironment environment;

    Discoverer(@NotNull Extender extender) {
        this.extender = extender;
        this.environment = extender.getEnvironment();
    }

    public Collection<DiscoveredExtension> discover() {
        return this.discover(Collections.emptySet());
    }

    public Collection<DiscoveredExtension> discover(@NotNull Collection<Path> alreadyKnown) {
        Path extensionsContainer = this.environment.getExtensionsContainer();
        Collection<DiscoveredExtension> extensions = Lists.newArrayList();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(
                extensionsContainer,
                path -> hasSupportedFileEnding(path) && !alreadyKnown.contains(path)
        )) {
            for (Path extensionFile : stream) {
                Optional<DiscoveredExtension> extensionDiscoveryResult = this.discoverExtension(extensionFile);

                if (extensionDiscoveryResult.isEmpty()) throw new IOException(); // TODO explicit exception
                extensions.add(extensionDiscoveryResult.get());
            }
        } catch (IOException | ParseException e) {
            // TODO wrap exception
            throw new RuntimeException(e);
        }

        return extensions;
    }

    public Optional<DiscoveredExtension> discoverExtension(@NotNull Path extensionFile) throws IOException, ParseException {
        try (JarFile file = new JarFile(extensionFile.toString())) {
            JarEntry extensionDescription = file.getJarEntry("extensions.json");
            if (extensionDescription == null) return Optional.empty();

            ExtensionDescription description = ExtensionDescription.fromJson(file.getInputStream(extensionDescription));
            return Optional.of(new DiscoveredExtension(extensionFile, description));
        }
    }

    public ExtensionEnvironment getEnvironment() {
        return this.environment;
    }

    public Extender getExtender() {
        return this.extender;
    }
}
