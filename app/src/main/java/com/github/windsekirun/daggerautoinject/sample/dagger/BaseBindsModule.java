package com.github.windsekirun.daggerautoinject.sample.dagger;

import android.arch.lifecycle.ViewModelProvider;

import com.github.windsekirun.daggerautoinject.sample.viewmodel.DaggerViewModelFactory;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class BaseBindsModule {
    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(DaggerViewModelFactory factory);
}