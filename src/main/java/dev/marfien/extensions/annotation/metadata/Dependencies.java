package dev.marfien.extensions.annotation.metadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface Dependencies {

    Dependency[] value();

    @interface Dependency {

        Class<?> value();

        boolean soft() default false;

    }

}
