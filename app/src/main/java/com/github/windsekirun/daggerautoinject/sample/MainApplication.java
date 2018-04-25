package com.github.windsekirun.daggerautoinject.sample;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;

import com.github.windsekirun.daggerautoinject.DaggerAutoInject;
import com.github.windsekirun.daggerautoinject.InjectApplication;
import com.github.windsekirun.daggerautoinject.sample.dagger.AppComponent;
import com.github.windsekirun.daggerautoinject.sample.dagger.DaggerAppComponent;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasBroadcastReceiverInjector;
import dagger.android.HasContentProviderInjector;
import dagger.android.HasServiceInjector;

@InjectApplication(component = AppComponent.class)
public class MainApplication extends Application implements HasActivityInjector, HasServiceInjector,
        HasBroadcastReceiverInjector, HasContentProviderInjector {

    @Inject DispatchingAndroidInjector<Activity> mActivityDispatchingAndroidInjector;
    @Inject DispatchingAndroidInjector<Service> mServiceDispatchingAndroidInjector;
    @Inject DispatchingAndroidInjector<BroadcastReceiver> mBroadcastReceiverDispatchingAndroidInjector;
    @Inject DispatchingAndroidInjector<ContentProvider> mContentProviderDispatchingAndroidInjector;

    @Override
    public void onCreate() {
        super.onCreate();

        final AppComponent appComponent = DaggerAppComponent.builder()
                .application(this)
                .build();

        DaggerAutoInject.init(this, appComponent);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return mActivityDispatchingAndroidInjector;
    }

    @Override
    public AndroidInjector<Service> serviceInjector() {
        return mServiceDispatchingAndroidInjector;
    }

    @Override
    public AndroidInjector<BroadcastReceiver> broadcastReceiverInjector() {
        return mBroadcastReceiverDispatchingAndroidInjector;
    }

    @Override
    public AndroidInjector<ContentProvider> contentProviderInjector() {
        return mContentProviderDispatchingAndroidInjector;
    }
}
