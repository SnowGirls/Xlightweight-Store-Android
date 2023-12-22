package com.tesla.modules.store.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;

public class TlStoreUtil {

    public static Context mContext;

    public static void setContext(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        if (mContext == null) {
            mContext = getApplication();
        }
        return mContext;
    }

    @SuppressLint("PrivateApi")
    private static Application getApplication() {
        try {
            Class<?> cActivityThread = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = cActivityThread.getDeclaredMethod("currentActivityThread");
            currentActivityThreadMethod.setAccessible(true);
            Object currentActivityThread = currentActivityThreadMethod.invoke(cActivityThread);
            Method getApplicationMethod = cActivityThread.getDeclaredMethod("getApplication");
            getApplicationMethod.setAccessible(true);
            return (Application) getApplicationMethod.invoke(currentActivityThread, new Object[]{});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("PrivateApi")
    private static Object getCurrentActivityThread() {
        try {
            Class<?> cActivityThread = Class.forName("android.app.ActivityThread");
            return cActivityThread.getMethod("currentActivityThread").invoke(cActivityThread);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * File Operations
     */

    public static String readFile(String path) {
        if (path == null || !new File(path).exists()) {
            return null;
        }
        FileReader reader = null;
        try {
            reader = new FileReader(path);
            StringBuilder builder = new StringBuilder();
            int len;
            char[] buf = new char[2 * 1024];
            while ((len = reader.read(buf)) != -1) {
                String str = new String(buf, 0, len);
                builder.append(str);
            }
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static boolean writeFile(String path, String content) {
        if (path == null || content == null) {
            return false;
        }
        boolean result = false;
        FileWriter writer = null;
        try {
            File file = new File(path);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            writer = new FileWriter(file, false);
            writer.write(content);
            writer.flush();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

}
