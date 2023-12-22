package com.xpel.xlightweight_store_android.util;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

public class ToastUtil {

    private static final String TAG = "ToastUtil";

    public static Context context;

    public static void show(String text) {
        android.util.Log.i(TAG, "show text: " + text);
        new android.os.Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, text, Toast.LENGTH_SHORT).show());
    }

}
