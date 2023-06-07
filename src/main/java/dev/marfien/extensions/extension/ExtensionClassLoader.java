package dev.marfien.extensions.extension;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Collection;

@ApiStatus.Internal
public class ExtensionClassLoader extends URLClassLoader {

    @ApiStatus.Internal
    public ExtensionClassLoader(String name, Collection<Path> paths, ClassLoader parent) {
        super(name, mapToURL(paths), parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve); // TODO lookup for classes in dependencies
    }

    private static URL[] mapToURL(@NotNull Collection<Path> paths) {
        return paths.stream().map(path -> {
            try {
                return path.toUri().toURL();
            } catch (MalformedURLException e) {
                throw new AssertionError(e);
            }
        }).toArray(URL[]::new);
    }


}
