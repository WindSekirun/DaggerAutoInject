package com.github.windsekirun.autoasync.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

/**
 * Created by florentchampigny on 24/01/2017.
 */

public class Constants {
    public static final String PACKAGE_NAME = "com.github.windsekirun.daggerautoinject";
    public static final String MAIN_CLASS_NAME = "DaggerAutoInject";
    public static final String ACTIVITY_MODULE = "ActivityModule";
    public static final String SERVICE_MODULE = "ServiceModule";
    public static final String FRAGMENT_MODULE = "FragmentModule";
    public static final String VIEWHOLDER_MODULE = "ViewModelModule";
    public static final String BROADCAST_MODULE = "BroadcastReceiverModule";
    public static final String CONTENT_MODULE = "ContentProviderModule";

    public static final String DAGGER = "Dagger";
    public static final String METHOD_INIT = "init";
    public static final String METHOD_CONTRIBUTE = "contribute_";
    public static final String METHOD_BIND = "bind_";
    public static final String METHOD_HANDLE_ACTIVITY = "handleActivity";
    public static final String PARAM_APPLICATION = "application";
    public static final String PARAM_COMPONENT = "component";

    public static final TypeName APPLICATION = ClassName.bestGuess("android.app.Application");
    public static final TypeName ACTIVITY = ClassName.bestGuess("android.app.Activity");
    public static final TypeName FRAGMENT_ACTIVITY = ClassName.bestGuess("android.support.v4.app.FragmentActivity");
    public static final TypeName FRAGMENT_MANAGER = ClassName.bestGuess("android.support.v4.app.FragmentManager");
    public static final TypeName ANDROID_SUPPORT_INJECTION = ClassName.bestGuess("dagger.android.support.AndroidSupportInjection");
    public static final TypeName ANDROID_INJECTION = ClassName.bestGuess("dagger.android.AndroidInjection");
    public static final TypeName ANDROID_LOG = ClassName.bestGuess("android.util.Log");
    public static final TypeName BUNDLE = ClassName.bestGuess("android.os.Bundle");
    public static final TypeName FRAGMENT = ClassName.bestGuess("android.support.v4.app.Fragment");
    public static final TypeName VIEWMODEL = ClassName.bestGuess("android.arch.lifecycle.ViewModel");

    public static final ClassName DAGGER_MODULE = ClassName.bestGuess("dagger.Module");
    public static final ClassName DAGGER_ANDROID_ANNOTATION = ClassName.bestGuess("dagger.android.ContributesAndroidInjector");
    public static final ClassName DAGGER_BINDS = ClassName.bestGuess("dagger.Binds");
    public static final ClassName DAGGER_INTOMAP = ClassName.bestGuess("dagger.multibindings.IntoMap");
    public static final ClassName DAGGER_VIEWMODEL_KEY = ClassName.bestGuess("com.github.windsekirun.autoasync.processor.holders.ViewModelKey");
}
