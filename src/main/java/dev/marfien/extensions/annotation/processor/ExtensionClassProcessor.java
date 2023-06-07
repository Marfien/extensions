package dev.marfien.extensions.annotation.processor;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

// TODO
@SupportedAnnotationTypes("dev.marfien.extensions.annotation.metadata.*")
@SupportedSourceVersion(SourceVersion.RELEASE_18)
@AutoService(Processor.class)
public class ExtensionClassProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {



        return true;
    }
}
