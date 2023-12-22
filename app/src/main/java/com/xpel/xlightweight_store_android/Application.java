package com.xpel.xlightweight_store_android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tesla.modules.store.util.TlStoreUtil;
import com.xpel.xlightweight_store_android.util.ToastUtil;


public class Application extends android.app.Application {
    
    public static final String TAG = "Application";
    
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ToastUtil.context = this;
        TlStoreUtil.setContext(this);

        // set default uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                android.util.Log.i(TAG, "uncaughtException: " + e);
            }
        });

        // set activity lifecycle callbacks
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                android.util.Log.i(TAG, "onActivityCreated: " + activity);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                android.util.Log.i(TAG, "onActivityStarted: " + activity);
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                android.util.Log.i(TAG, "onActivityResumed: " + activity);
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                android.util.Log.i(TAG, "onActivityPaused: " + activity);
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                android.util.Log.i(TAG, "onActivityStopped: " + activity);
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
                android.util.Log.i(TAG, "onActivitySaveInstanceState: " + activity);
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                android.util.Log.i(TAG, "onActivityDestroyed: " + activity);
            }
        });
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
