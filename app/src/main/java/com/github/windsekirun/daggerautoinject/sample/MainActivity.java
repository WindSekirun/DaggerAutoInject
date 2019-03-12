package com.github.windsekirun.daggerautoinject.sample;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.florent37.rxretrojsoup.sample.R;
import com.github.windsekirun.daggerautoinject.InjectActivity;
import com.github.windsekirun.daggerautoinject.sample.viewmodel.MainViewModel;

import javax.inject.Inject;

@InjectActivity
public class MainActivity extends BaseActivity {

    @Inject
    SharedPreferences sharedPreferences;
    @Inject ViewModelProvider.Factory mViewModelFactory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainViewModel mainViewModel = ViewModelProviders.of(this, mViewModelFactory).get(MainViewModel.class);
        mainViewModel.hashCode();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, MainFragment.newInstance())
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println(sharedPreferences.getAll());
    }

}
