package com.github.windsekirun.autoasync.processor;

import com.github.windsekirun.daggerautoinject.InjectActivity;
import com.github.windsekirun.daggerautoinject.InjectApplication;
import com.github.windsekirun.daggerautoinject.InjectBroadcastReceiver;
import com.github.windsekirun.daggerautoinject.InjectContentProvider;
import com.github.windsekirun.daggerautoinject.InjectFragment;
import com.github.windsekirun.daggerautoinject.InjectService;
import com.github.windsekirun.daggerautoinject.InjectViewModel;
import com.github.windsekirun.daggerautoinject.ViewModelKey;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

@SupportedAnnotationTypes({
        "com.github.windsekirun.daggerautoinject.InjectActivity",
        "com.github.windsekirun.daggerautoinject.InjectFragment",
        "com.github.windsekirun.daggerautoinject.InjectApplication",
        "com.github.windsekirun.daggerautoinject.InjectViewModel",
        "com.github.windsekirun.daggerautoinject.InjectService",
        "com.github.windsekirun.daggerautoinject.InjectBroadcastReceiver",
        "com.github.windsekirun.daggerautoinject.InjectContentProvider"
})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@AutoService(javax.annotation.processing.Processor.class)
public class DaggerAutoInjectProcessor extends AbstractProcessor {
    private Map<ClassName, ContributesHolder> mActivityHolders = new HashMap<>();
    private Map<ClassName, ContributesHolder> mFragmentHolders = new HashMap<>();
    private Map<ClassName, ContributesHolder> mViewModelHolders = new HashMap<>();
    private Map<ClassName, ContributesHolder> mServiceHolders = new HashMap<>();
    private Map<ClassName, ContributesHolder> mBroadcastHolders = new HashMap<>();
    private Map<ClassName, ContributesHolder> mContentHolders = new HashMap<>();

    private ApplicationHolder mApplicationHolder;
    private Filer mFiler;

    private static TypeMirror getComponent(InjectApplication annotation) {
        try {
            annotation.component(); // this should throw
        } catch (MirroredTypeException mte) {
            return mte.getTypeMirror();
        }
        return null; // can this ever happen ??
    }

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        mFiler = env.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        processAnnotations(env);

        if (mApplicationHolder != null) {
            writeHoldersOnJavaFile();
        }
        return true;
    }

    private void processAnnotations(RoundEnvironment env) {
        Utils.processHolders(env, InjectActivity.class, mActivityHolders);
        Utils.processHolders(env, InjectFragment.class, mFragmentHolders);
        Utils.processHolders(env, InjectService.class, mServiceHolders);
        Utils.processHolders(env, InjectBroadcastReceiver.class, mBroadcastHolders);
        Utils.processHolders(env, InjectContentProvider.class, mContentHolders);
        Utils.processHolders(env, InjectViewModel.class, mViewModelHolders);

        for (Element element : env.getElementsAnnotatedWith(InjectApplication.class)) {
            final ClassName classFullName = ClassName.get((TypeElement) element);
            final String className = element.getSimpleName().toString();
            final TypeMirror componentClass = getComponent(element.getAnnotation(InjectApplication.class));

            mApplicationHolder = new ApplicationHolder(element, classFullName, className);
            mApplicationHolder.setComponentClass(componentClass);
        }
    }

    private void writeHoldersOnJavaFile() {
        Utils.constructContributesAndroidInjector(Constants.ACTIVITY_MODULE, mActivityHolders.values(), mFiler);
        Utils.constructContributesAndroidInjector(Constants.SERVICE_MODULE, mServiceHolders.values(), mFiler);
        Utils.constructContributesAndroidInjector(Constants.FRAGMENT_MODULE, mFragmentHolders.values(), mFiler);
        Utils.constructContributesAndroidInjector(Constants.BROADCAST_MODULE, mBroadcastHolders.values(), mFiler);
        Utils.constructContributesAndroidInjector(Constants.CONTENT_MODULE, mContentHolders.values(), mFiler);
        constructViewHolderModule();
        construct();

        mFragmentHolders.clear();
        mActivityHolders.clear();
        mViewModelHolders.clear();
        mServiceHolders.clear();
        mBroadcastHolders.clear();
        mContentHolders.clear();
        mApplicationHolder = null;
    }

    private void constructViewHolderModule() {
        final TypeSpec.Builder builder = TypeSpec.classBuilder(Constants.VIEWHOLDER_MODULE)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(Constants.DAGGER_MODULE);

        for (ContributesHolder contributesHolder : mViewModelHolders.values()) {
            TypeName typeName = contributesHolder.classNameComplete;
            String parameterName = String.valueOf(contributesHolder.className.charAt(0)).toLowerCase() +
                    contributesHolder.className.substring(1);

            String canonicalName = getCanonicalName(contributesHolder.classNameComplete);

            builder.addMethod(MethodSpec.methodBuilder(Constants.METHOD_BIND + contributesHolder.className)
                    .addAnnotation(Constants.DAGGER_BINDS)
                    .addParameter(typeName, parameterName)
                    .addAnnotation(Constants.DAGGER_INTOMAP)
                    .addAnnotation(AnnotationSpec.builder(ViewModelKey.class)
                            .addMember("value", "$S", canonicalName).build())
                    .addModifiers(Modifier.ABSTRACT)
                    .returns(Constants.VIEWMODEL)
                    .build()
            );
        }

        final TypeSpec newClass = builder.build();
        final JavaFile javaFile = JavaFile.builder(Constants.PACKAGE_NAME, newClass).build();

        try {
            javaFile.writeTo(System.out);
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCanonicalName(ClassName className) {
        return className.enclosingClassName() != null
                ? (getCanonicalName(className.enclosingClassName()) + '.' + className.simpleName())
                : (className.packageName().isEmpty() ? className.simpleName()
                : className.packageName() + '.' + className.simpleName());
    }

    private void construct() {
        final TypeSpec.Builder builder = TypeSpec.classBuilder(Constants.MAIN_CLASS_NAME)
                .addModifiers(Modifier.PUBLIC);

        builder.addField(FieldSpec.builder(ClassName.get(String.class), "TAG", Modifier.FINAL, Modifier.PUBLIC, Modifier.STATIC)
                .initializer("\"" + Constants.MAIN_CLASS_NAME + "\"").build());

        builder.addMethod(
                MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)
                        .build()
        );

        //final ClassName daggerComponent = findDaggerComponent(mApplicationHolder.componentClass);
        final ClassName component = findComponent(mApplicationHolder.componentClass);

        final MethodSpec.Builder methodInit = MethodSpec.methodBuilder(Constants.METHOD_INIT)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        if (mApplicationHolder != null) {

            methodInit
                    .addParameter(mApplicationHolder.classNameComplete, Constants.PARAM_APPLICATION)
                    .addParameter(component, Constants.PARAM_COMPONENT);

            methodInit.addStatement("$L.inject($L)", Constants.PARAM_COMPONENT, Constants.PARAM_APPLICATION);
        }


        methodInit.addStatement("application.registerActivityLifecycleCallbacks(new $T.ActivityLifecycleCallbacks() {\n" +
                        "            @Override\n" +
                        "            public void onActivityCreated($T activity, $T savedInstanceState) {\n" +
                        "                " + Constants.METHOD_HANDLE_ACTIVITY + "(activity);\n" +
                        "            }\n" +
                        "\n" +
                        "            @Override\n" +
                        "            public void onActivityStarted(Activity activity) {\n" +
                        "\n" +
                        "            }\n" +
                        "\n" +
                        "            @Override\n" +
                        "            public void onActivityResumed(Activity activity) {\n" +
                        "\n" +
                        "            }\n" +
                        "\n" +
                        "            @Override\n" +
                        "            public void onActivityPaused(Activity activity) {\n" +
                        "\n" +
                        "            }\n" +
                        "\n" +
                        "            @Override\n" +
                        "            public void onActivityStopped(Activity activity) {\n" +
                        "\n" +
                        "            }\n" +
                        "\n" +
                        "            @Override\n" +
                        "            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {\n" +
                        "\n" +
                        "            }\n" +
                        "\n" +
                        "            @Override\n" +
                        "            public void onActivityDestroyed(Activity activity) {\n" +
                        "\n" +
                        "            }\n" +
                        "        });",

                Constants.APPLICATION,
                Constants.ACTIVITY,
                Constants.BUNDLE
        );

        builder.addMethod(methodInit.build());

        final MethodSpec.Builder methodHandleActivity = MethodSpec.methodBuilder(Constants.METHOD_HANDLE_ACTIVITY)
                .addModifiers(Modifier.PROTECTED, Modifier.STATIC)
                .addParameter(Constants.ACTIVITY, "activity");

        methodHandleActivity.addCode("try {\n" +
                        "            $T.inject(activity);\n" +
                        "        } catch (Exception e){\n" +
                        "            $T.d(TAG, activity.getClass().toString()+\" non injected\");\n" +
                        "        }\n" +
                        "        if (activity instanceof $T) {\n" +
                        "            final $T supportFragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();\n" +
                        "            supportFragmentManager.registerFragmentLifecycleCallbacks(\n" +
                        "                    new FragmentManager.FragmentLifecycleCallbacks() {\n" +
                        "                        @Override\n" +
                        "                        public void onFragmentCreated(FragmentManager fm, $T f, $T savedInstanceState) {\n" +
                        "                            try {\n" +
                        "                                $T.inject(f);\n" +
                        "                            } catch (Exception e){\n" +
                        "                                Log.d(TAG, f.getClass().toString()+\" non injected\");\n" +
                        "                            }\n" +
                        "                        }\n" +
                        "                    }, true);\n" +
                        "        }",

                Constants.ANDROID_INJECTION,
                Constants.ANDROID_LOG,
                Constants.FRAGMENT_ACTIVITY,
                Constants.FRAGMENT_MANAGER,
                Constants.FRAGMENT,
                Constants.BUNDLE,
                Constants.ANDROID_SUPPORT_INJECTION
        );

        builder.addMethod(methodHandleActivity.build());

        final TypeSpec newClass = builder.build();

        final JavaFile javaFile = JavaFile.builder(Constants.PACKAGE_NAME, newClass).build();

        try {
            javaFile.writeTo(System.out);
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ClassName findComponent(TypeMirror typeMirror) {
        final ClassName typeName = (ClassName) TypeName.get(typeMirror);
        final String packageName = typeName.packageName();
        final String className = typeName.simpleName();
        return ClassName.bestGuess(packageName + "." + className);
    }
}