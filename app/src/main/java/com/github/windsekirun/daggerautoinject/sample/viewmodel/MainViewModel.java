package com.github.windsekirun.daggerautoinject.sample.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.github.windsekirun.daggerautoinject.InjectViewModel;

import javax.inject.Inject;

/**
 * DaggerAutoInject
 * Class: MainViewModel
 * Created by Pyxis on 2018-02-02.
 * <p>
 * Description:
 */

@InjectViewModel
public class MainViewModel extends ViewModel {

    @Inject
    public MainViewModel() {

    }
}
