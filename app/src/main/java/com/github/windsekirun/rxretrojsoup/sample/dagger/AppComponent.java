package com.github.windsekirun.rxretrojsoup.sample.dagger;

import android.app.Application;

import com.github.windsekirun.daggerautoinject.ActivityModule;
import com.github.windsekirun.daggerautoinject.FragmentModule;
import com.github.windsekirun.rxretrojsoup.sample.MainApplication;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        AppModule.class,

        AndroidInjectionModule.class,
        AndroidSupportInjectionModule.class,

        ActivityModule.class,
        FragmentModule.class,
})
public interface AppComponent {
    void inject(MainApplication application);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        AppComponent build();
    }
}
