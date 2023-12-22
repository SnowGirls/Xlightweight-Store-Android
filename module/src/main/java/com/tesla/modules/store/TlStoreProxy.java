package com.tesla.modules.store;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tesla.modules.store.Implements.TlSharedPreferences;
import com.tesla.modules.store.Implements.TlUserFiles;

public class TlStoreProxy implements TlStoreInterface {

    public enum PROXY_TYPE {SP, FILE}

    public TlStoreInterface myProxy;

    public TlStoreProxy(@NonNull String moduleName, @Nullable String aesKey, @Nullable String aesIV, @Nullable PROXY_TYPE type) {
        TlStoreInterface proxy = null;
        if (type == PROXY_TYPE.SP) {
            proxy = new TlSharedPreferences(moduleName, aesKey, aesIV);
        } else if (type == PROXY_TYPE.FILE) {
            proxy = new TlUserFiles(moduleName, aesKey, aesIV);
        } else {
            proxy = new TlSharedPreferences(moduleName, aesKey, aesIV); // DEFAULT
        }
        this.myProxy = proxy;
    }

    @Override
    public boolean contains(String key) {
        return this.myProxy.contains(key);
    }

    @Override
    public void removeKey(String key) {
        this.myProxy.removeKey(key);
    }

    @Override
    public String getString(String key, String defValue) {
        return this.myProxy.getString(key, defValue);
    }

    @Override
    public int getInt(String key, int defValue) {
        return this.myProxy.getInt(key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return this.myProxy.getLong(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return this.myProxy.getFloat(key, defValue);
    }

    @Override
    public double getDouble(String key, double defValue) {
        return this.myProxy.getDouble(key, defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return this.myProxy.getBoolean(key, defValue);
    }

    @Override
    public void setString(String key, String value) {
        this.myProxy.setString(key, value);
    }

    @Override
    public void setInt(String key, int value) {
        this.myProxy.setInt(key, value);
    }

    @Override
    public void setLong(String key, long value) {
        this.myProxy.setLong(key, value);
    }

    @Override
    public void setFloat(String key, float value) {
        this.myProxy.setFloat(key, value);
    }

    @Override
    public void setDouble(String key, double value) {
        this.myProxy.setDouble(key, value);
    }

    @Override
    public void setBoolean(String key, boolean value) {
        this.myProxy.setBoolean(key, value);
    }
}
