package dev.marfien.extensions.annotation.metadata;

public @interface Dependencies {

    Dependency[] value();

    @interface Dependency {

        Class<?> value();

        boolean soft() default false;

    }

}
