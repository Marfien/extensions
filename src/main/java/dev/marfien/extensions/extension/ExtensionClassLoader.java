package dev.marfien.extensions.extension;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;

@ApiStatus.Internal
public class ExtensionClassLoader extends URLClassLoader {

    private final Collection<ExtensionClassLoader> childCLassLoaders;

    public ExtensionClassLoader(String name, Collection<ExtensionClassLoader> children, ClassLoader parent) {
        super(name, new URL[0], parent);

        this.childCLassLoaders = new ArrayList<>(children);
    }

    public void addChild(@NotNull ExtensionClassLoader classLoader) {
        this.childCLassLoaders.add(classLoader);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException ignored) {
            // save to ignore
            // just looking through every child class loader
        }

        for (ExtensionClassLoader childCLassLoader : this.childCLassLoaders) {
            try {
                childCLassLoader.loadClass(name, resolve);
            } catch (ClassNotFoundException ignored) {
                // save to ignore
                // looking through every child
            }
        }

        throw new ClassNotFoundException(name);
    }


}
