package dev.marfien.extensions.annotation.metadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Extension {

    String id() default UNSET;

    String version() default UNSET;

    String name() default UNSET;

    String UNSET = "N/A";

}
