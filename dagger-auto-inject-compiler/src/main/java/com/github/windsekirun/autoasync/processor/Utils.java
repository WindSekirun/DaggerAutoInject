package com.github.windsekirun.autoasync.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created by pyxis on 18. 4. 25.
 */

public class Utils {

    static <A extends Annotation> void processHolders(RoundEnvironment env, Class<A> cls, Map<ClassName, ContributesHolder> map) {
        for (Element element : env.getElementsAnnotatedWith(cls)) {
            final ClassName classFullName = ClassName.get((TypeElement) element);
            final String className = element.getSimpleName().toString();
            map.put(classFullName, new ContributesHolder(element, classFullName, className));
        }
    }

    static void constructContributesAndroidInjector(String className, Collection<ContributesHolder> holders, Filer filer) {
        final TypeSpec.Builder builder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(Constants.DAGGER_MODULE);

        for (ContributesHolder contributesHolder : holders) {
            builder.addMethod(MethodSpec.methodBuilder(Constants.METHOD_CONTRIBUTE + contributesHolder.className)
                    .addAnnotation(Constants.DAGGER_ANDROID_ANNOTATION)
                    .addModifiers(Modifier.ABSTRACT)
                    .returns(contributesHolder.classNameComplete)
                    .build()
            );
        }

        final TypeSpec newClass = builder.build();
        final JavaFile javaFile = JavaFile.builder(Constants.PACKAGE_NAME, newClass).build();

        try {
            javaFile.writeTo(System.out);
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
