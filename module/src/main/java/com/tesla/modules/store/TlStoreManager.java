package com.tesla.modules.store;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tesla.modules.store.Implements.TlSharedPreferences;
import com.tesla.modules.store.Implements.TlStoreBase;

import java.util.HashMap;

public class TlStoreManager {

    private static volatile TlStoreManager instance;

    // https://en.wikipedia.org/wiki/Double-checked_locking
    public static TlStoreManager getInstance() {
        TlStoreManager localRef = instance;
        if (localRef == null) {
            synchronized (TlStoreManager.class) {
                localRef = instance;
                if (localRef == null) {
                    instance = localRef = new TlStoreManager();
                }
            }
        }
        return localRef;
    }

    /**
     * Initial methods
     */

    final public HashMap<String, HashMap<String, Object>> mSpecs = new HashMap<>();

    public void register(@NonNull String module, @Nullable String aesKey, @Nullable String aesIV,
                         @Nullable Boolean isAsyncApply, @Nullable Boolean isKeepKeyClearText) {
        HashMap<String, Object> spec = new HashMap<>();
        if (aesKey != null) spec.put("aesKey", aesKey);
        if (aesIV != null) spec.put("aesIV", aesIV);
        if (isAsyncApply != null) spec.put("isAsyncApply", isAsyncApply);
        if (isKeepKeyClearText != null) spec.put("isKeepKeyClearText", isKeepKeyClearText);
        mSpecs.put(module, spec);
    }

    final public HashMap<String, TlStoreProxy> mStores = new HashMap<>();

    private TlStoreInterface assureExisted(@NonNull String module) {
        HashMap<String, Object> spec = mSpecs.get(module);
        if (spec == null) {
            return null;
        }
        TlStoreProxy store = mStores.get(module);
        if (store == null) {
            synchronized (mStores) {
                store = mStores.get(module);
                if (store == null) {
                    String aesKey = (String) spec.get("aesKey");
                    String aesIV = (String) spec.get("aesIV");
                    Boolean isAsyncApply = (Boolean) spec.get("isAsyncApply");
                    Boolean isKeepKeyClearText = (Boolean) spec.get("isKeepKeyClearText");

                    store = new TlStoreProxy(module, aesKey, aesIV, null);
                    mStores.put(module, store);

                    // use apply() instead of commit() for sp
                    if (store.myProxy instanceof TlSharedPreferences) {
                        boolean isUseApply = isAsyncApply != null && isAsyncApply;
                        if (isUseApply) {
                            ((TlSharedPreferences) store.myProxy).isAsyncApply = true;
                        }
                    }

                    // do not encode name
                    if (store.myProxy instanceof TlStoreBase) {
                        boolean isKeepKeyClear = isKeepKeyClearText != null && isKeepKeyClearText;
                        if (isKeepKeyClear) {
                            ((TlStoreBase) store.myProxy).keyTransformer = null;
                        }
                    }
                }
            }
        }
        return store;
    }

    public void unregister(@NonNull String module) {
        mStores.remove(module);
    }

    /**
     * Key-Value methods
     */

    public boolean contains(String module, String key) {
        TlStoreInterface sp = assureExisted(module);
        if (sp == null) return false;
        return sp.contains(key);
    }

    public void removeKey(String module, String key) {
        TlStoreInterface sp = assureExisted(module);
        if (sp == null) return;
        sp.removeKey(key);
    }

    public String getString(String module, String key, String defValue) {
        TlStoreInterface sp = assureExisted(module);
        if (sp == null) return defValue;
        return sp.getString(key, defValue);
    }

    public int getInt(String module, String key, int defValue) {
        TlStoreInterface sp = assureExisted(module);
        if (sp == null) return defValue;
        return sp.getInt(key, defValue);
    }

    public long getLong(String module, String key, long defValue) {
        TlStoreInterface sp = assureExisted(module);
        if (sp == null) return defValue;
        return sp.getLong(key, defValue);
    }

    public float getFloat(String module, String key, float defValue) {
        TlStoreInterface sp = assureExisted(module);
        if (sp == null) return defValue;
        return sp.getFloat(key, defValue);
    }

    public double getDouble(String module, String key, double defValue) {
        TlStoreInterface sp = assureExisted(module);
        if (sp == null) return defValue;
        return sp.getDouble(key, defValue);
    }

    public boolean getBoolean(String module, String key, boolean defValue) {
        TlStoreInterface sp = assureExisted(module);
        if (sp == null) return defValue;
        return sp.getBoolean(key, defValue);
    }

    public void setString(String module, String key, String value) {
        TlStoreInterface sp = assureExisted(module);
        if (sp == null) return;
        sp.setString(key, value);
    }

    public void setInt(String module, String key, int value) {
        TlStoreInterface sp = assureExisted(module);
        if (sp == null) return;
        sp.setInt(key, value);
    }

    public void setLong(String module, String key, long value) {
        TlStoreInterface sp = assureExisted(module);
        if (sp == null) return;
        sp.setLong(key, value);
    }

    public void setFloat(String module, String key, float value) {
        TlStoreInterface sp = assureExisted(module);
        if (sp == null) return;
        sp.setFloat(key, value);
    }

    public void setDouble(String module, String key, double value) {
        TlStoreInterface sp = assureExisted(module);
        if (sp == null) return;
        sp.setDouble(key, value);
    }

    public void setBoolean(String module, String key, boolean value) {
        TlStoreInterface sp = assureExisted(module);
        if (sp == null) return;
        sp.setBoolean(key, value);
    }
}
