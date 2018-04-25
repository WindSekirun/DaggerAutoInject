package com.github.windsekirun.daggerautoinject.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.windsekirun.daggerautoinject.InjectBroadcastReceiver;

import dagger.android.AndroidInjection;

/**
 * Created by pyxis on 18. 4. 25.
 */

@InjectBroadcastReceiver
public class MainBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AndroidInjection.inject(this, context);
    }
}
