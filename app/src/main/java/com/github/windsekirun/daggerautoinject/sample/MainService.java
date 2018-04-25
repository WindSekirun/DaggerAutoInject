package com.github.windsekirun.daggerautoinject.sample;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.github.windsekirun.daggerautoinject.InjectService;

import dagger.android.AndroidInjection;

/**
 * Created by pyxis on 18. 4. 25.
 */

@InjectService
public class MainService extends Service {

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
