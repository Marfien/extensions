package dev.marfien.extensions.annotation.inject;

import com.google.inject.BindingAnnotation;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.internal.Annotations;
import com.google.inject.name.Named;
import dev.marfien.extensions.PropertyKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@BindingAnnotation
public @interface RandomStringValue {

    int LENGTH = Integer.getInteger(PropertyKey.RANDOM_STRING_LENGTH);

    Template value() default Template.CHARACTERS_NUMBER;


    enum Template {

        NUMBERS("1234567890"),
        SPECIAL_CHARACTERS("~!@#$%^&*-_+=()[]{}<>,.?/`\\|"),

        LOWERCASE_CHARACTERS("abcdefghijklmnopqrstuvwxyz"),
        UPPERCASE_CHARACTERS(LOWERCASE_CHARACTERS.characters.toUpperCase()),
        CHARACTERS(LOWERCASE_CHARACTERS.characters + UPPERCASE_CHARACTERS.characters),

        LOWERCASE_NUMBER(LOWERCASE_CHARACTERS.characters + NUMBERS.characters),
        UPPERCASE_NUMBER(UPPERCASE_CHARACTERS.characters + NUMBERS.characters),
        CHARACTERS_NUMBER(CHARACTERS.characters + NUMBERS.characters),

        LOWERCASE_SPECIAL_NUMBER(LOWERCASE_NUMBER.characters + SPECIAL_CHARACTERS.characters),
        UPPERCASE_SPECIAL_NUMBER(UPPERCASE_NUMBER.characters + SPECIAL_CHARACTERS.characters),
        CHARACTERS_SPECIAL_NUMBER(CHARACTERS_NUMBER.characters + SPECIAL_CHARACTERS.characters);

        private final String characters;
        private final Annotation annotationImpl;

        Template(String characters) {
            this.characters = characters;
            this.annotationImpl = new RandomStringValueImpl(this);
        }

        public String getPossibleCharacters() {
            return this.characters;
        }

        public Annotation getAnnotationImpl() {
            return this.annotationImpl;
        }

        public String generateString(int length) {
            Random random = ThreadLocalRandom.current();
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < length; i++) {
                builder.append(this.characters.charAt(random.nextInt(this.characters.length())));
            }

            return builder.toString();
        }

        public static void injectBindings(@NotNull Function<Class<String>, AnnotatedBindingBuilder<String>> bind) {
            for (Template template : values()) {
                bind.apply(String.class)
                        .annotatedWith(template.annotationImpl)
                        .toProvider(() -> template.generateString(LENGTH));
            }
        }

    }

    @ApiStatus.Internal
    class RandomStringValueImpl implements RandomStringValue {

        private final Template value;

        private RandomStringValueImpl(@NotNull Template value) {
            this.value = value;
        }

        @Override
        public Template value() {
            return this.value;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return RandomStringValue.class;
        }

        @Override
        public int hashCode() {
            // This is specified in java.lang.Annotation.
            return (127 * "value".hashCode()) ^ this.value.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof RandomStringValue rsv)) return false;

            return this.value.equals(rsv.value());
        }

        @Override
        public String toString() {
            return '@' + RandomStringValue.class.getName() + '(' + Annotations.memberValueString("value", this.value) + ')';
        }
    }

}
