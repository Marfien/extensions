package dev.marfien.extensions.extension;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

public record ExtensionDescription(
        @NotNull String id,
        @NotNull String version,
        @NotNull String name,
        @Nullable String author,
        @NotNull Collection<Dependency> dependencies,
        @NotNull Collection<Library> libraries
        ) {

    public record Dependency(@NotNull String id, boolean soft) {
        public Dependency(@NotNull String id) {
            this(id, false);
        }
    }

    public record Library(@NotNull String group, @NotNull String name, @NotNull String version) {
        public static Library fromString(@NotNull String dependencyString) {
            String[] args = dependencyString.split(":");

            if (args.length != 3)
                throw new IllegalArgumentException(
                        "The dependency string '%s' is in a wrong format. It needs to be in the format of 'group:name:version'.".formatted(dependencyString));

            return new Library(args[0], args[1], args[2]);
        }

        public String dependencyString() {
            return "%s:%s:%s".formatted(this.group, this.name, this.version);
        }
    }

    public static ExtensionDescription fromJson(@NotNull InputStream input) throws ParseException, IOException {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(new InputStreamReader(input));

        String id = (String) object.get("id");
        String version = (String) object.get("version");
        String name = (String) object.get("name");
        String author = (String) object.get("author");

        String entrypoint = (String) object.get("entrypoint");

        return new ExtensionDescription(id, version, name, author, null, null); // TODO implement entrypoint and dependencies/libraries
    }

}
