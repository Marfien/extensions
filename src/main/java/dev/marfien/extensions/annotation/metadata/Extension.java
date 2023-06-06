package dev.marfien.extensions.annotation.metadata;

public @interface Extension {

    String id() default UNSET;

    String version() default UNSET;

    String name() default UNSET;

    String UNSET = "N/A";

}
