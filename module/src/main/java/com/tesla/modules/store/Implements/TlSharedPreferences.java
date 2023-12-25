package com.tesla.modules.store.Implements;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tesla.modules.store.TlStoreInterface;
import com.tesla.modules.store.util.TlStoreUtil;

public class TlSharedPreferences extends TlStoreBase implements TlStoreInterface {

    public boolean isAsyncApply = false;

    private SharedPreferences preferences;

    public TlSharedPreferences(@NonNull String moduleName, @Nullable String aesKey, @Nullable String aesIV) {
        super(moduleName, aesKey, aesIV);
        this.preferences = TlStoreUtil.getContext().getSharedPreferences(moduleName, Context.MODE_PRIVATE);
    }

    @Override
    public boolean contains(String key) {
        if (key == null) return false;
        return preferences.contains(encodeKeyIfNeeded(key));
    }

    @Override
    public String getString(String key, String defValue) {
        if (key == null) return defValue;
        if (this.valueDecoder != null) {
            // Try to decode value if needed
            String value = preferences.getString(encodeKeyIfNeeded(key), null);
            if (value != null) {
                return this.valueDecoder.apply(value);
            }
            return defValue;
        }
        // Return the clear value
        return preferences.getString(encodeKeyIfNeeded(key), defValue);
    }

    @Override
    public int getInt(String key, int defValue) {
        if (key == null) return defValue;
        if (this.valueDecoder != null) {
            // Try to decode value if needed
            String value = getString(key, null);
            if (value != null) {
                try {
                    return Integer.parseInt(value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return defValue;
        }
        // Return the clear value
        return preferences.getInt(encodeKeyIfNeeded(key), defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        if (key == null) return defValue;
        if (this.valueDecoder != null) {
            // Try to decode value if needed
            String value = getString(key, null);
            if (value != null) {
                try {
                    return Long.parseLong(value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return defValue;
        }
        // Return the clear value
        return preferences.getLong(encodeKeyIfNeeded(key), defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        if (key == null) return defValue;
        if (this.valueDecoder != null) {
            // Try to decode value if needed
            String value = getString(key, null);
            if (value != null) {
                try {
                    return Float.parseFloat(value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return defValue;
        }
        // Return the clear value
        return preferences.getFloat(encodeKeyIfNeeded(key), defValue);
    }

    @Override
    public double getDouble(String key, double defValue) {
        if (key == null) return defValue;
        if (this.valueDecoder != null) {
            // Try to decode value if needed
            String value = getString(key, null);
            if (value != null) {
                try {
                    return Double.parseDouble(value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return defValue;
        }
        // Return the clear value
        try {
            String value = preferences.getString(encodeKeyIfNeeded(key), null);
            if (value != null) {
                return Double.parseDouble(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        if (key == null) return defValue;
        if (this.valueDecoder != null) {
            // Try to decode value if needed
            String value = getString(key, null);
            if (value != null) {
                try {
                    return Boolean.parseBoolean(value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return defValue;
        }
        // Return the clear value
        return preferences.getBoolean(encodeKeyIfNeeded(key), defValue);
    }

    @Override
    public void removeKey(String key) {
        if (key == null) return;
        if (!contains(key)) return;
        doCommit(preferences.edit().remove(encodeKeyIfNeeded(key)));
    }

    @Override
    public void setString(String key, String value) {
        if (key == null) return;
        // Try to encode value if needed
        if (this.valueEncoder != null && value != null) {
            value = this.valueEncoder.apply(value);
        }
        // Just save the clear value
        doCommit(preferences.edit().putString(encodeKeyIfNeeded(key), value));
    }

    @Override
    public void setInt(String key, int value) {
        if (key == null) return;
        if (this.valueEncoder != null) {
            // Try to encode value if needed
            String result = this.valueEncoder.apply(String.valueOf(value));
            doCommit(preferences.edit().putString(encodeKeyIfNeeded(key), result));
            return;
        }
        // Just save the clear value
        doCommit(preferences.edit().putInt(encodeKeyIfNeeded(key), value));
    }

    @Override
    public void setLong(String key, long value) {
        if (key == null) return;
        if (this.valueEncoder != null) {
            // Try to encode value if needed
            String result = this.valueEncoder.apply(String.valueOf(value));
            doCommit(preferences.edit().putString(encodeKeyIfNeeded(key), result));
            return;
        }
        // Just save the clear value
        doCommit(preferences.edit().putLong(encodeKeyIfNeeded(key), value));
    }

    @Override
    public void setFloat(String key, float value) {
        if (key == null) return;
        if (this.valueEncoder != null) {
            // Try to encode value if needed
            String result = this.valueEncoder.apply(String.valueOf(value));
            doCommit(preferences.edit().putString(encodeKeyIfNeeded(key), result));
            return;
        }
        // Just save the clear value
        doCommit(preferences.edit().putFloat(encodeKeyIfNeeded(key), value));
    }

    @Override
    public void setDouble(String key, double value) {
        if (key == null) return;
        if (this.valueEncoder != null) {
            // Try to encode value if needed
            String result = this.valueEncoder.apply(String.valueOf(value));
            doCommit(preferences.edit().putString(encodeKeyIfNeeded(key), result));
            return;
        }
        // Just save the clear value
        doCommit(preferences.edit().putString(encodeKeyIfNeeded(key), String.valueOf(value)));
    }

    @Override
    public void setBoolean(String key, boolean value) {
        if (key == null) return;
        if (this.valueEncoder != null) {
            // Try to encode value if needed
            String result = this.valueEncoder.apply(String.valueOf(value));
            doCommit(preferences.edit().putString(encodeKeyIfNeeded(key), result));
            return;
        }
        // Just save the clear value
        doCommit(preferences.edit().putBoolean(encodeKeyIfNeeded(key), value));
    }

    // do commit or apply
    /// TODO ... commit() & apply() 都需要在IO失败的时机/没同步到文件的时机，来检查是否需要重试
    private void doCommit(SharedPreferences.Editor editor) {
        if (isAsyncApply) {
            editor.apply();
        } else {
            editor.commit();
        }
    }
}