package com.tesla.modules.store.Implements;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tesla.modules.store.TlStoreInterface;
import com.tesla.modules.store.util.TlStoreUtil;

import java.io.File;

public class TlUserFiles extends TlStoreBase implements TlStoreInterface {

    private String directory;

    public TlUserFiles(@NonNull String moduleName, @Nullable String aesKey, @Nullable String aesIV) {
        super(moduleName, aesKey, aesIV);
        directory = TlStoreUtil.getContext().getFilesDir().getAbsolutePath() + "/" + moduleName + "/";
    }

    // IMPORTANT NOTE: key as the file name, so the key cannot cannot contains special characters !!!

    @NonNull
    private String getFullPath(String key) {
        String name = encodeKeyIfNeeded(key);
        return directory + name;
    }

    @Override
    public boolean contains(String key) {
        if (key == null) return false;
        return new File(getFullPath(key)).exists();
    }

    @Override
    public void removeKey(String key) {
        if (key == null) return;
        new File(getFullPath(key)).delete();
    }

    @Override
    public String getString(String key, String defValue) {
        if (key == null) return defValue;
        String path = getFullPath(key);
        String value = TlStoreUtil.readFile(path);
        if (this.valueDecoder != null && value != null) {
            // Try to decode value if needed
            value = this.valueDecoder.apply(value);
        }
        return value != null ? value : defValue;
    }

    @Override
    public int getInt(String key, int defValue) {
        if (key == null) return defValue;
        String value = getString(key, null);
        if (value == null) return defValue;
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        if (key == null) return defValue;
        String value = getString(key, null);
        if (value == null) return defValue;
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

    @Override
    public float getFloat(String key, float defValue) {
        if (key == null) return defValue;
        String value = getString(key, null);
        if (value == null) return defValue;
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

    @Override
    public double getDouble(String key, double defValue) {
        if (key == null) return defValue;
        String value = getString(key, null);
        if (value == null) return defValue;
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        if (key == null) return defValue;
        String value = getString(key, null);
        if (value == null) return defValue;
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

    @Override
    public void setString(String key, String value) {
        if (key == null) return;
        // Try to encode value if needed
        if (this.valueEncoder != null && value != null) {
            value = this.valueEncoder.apply(value);
        }
        if (value == null) return;
        String path = getFullPath(key);
        TlStoreUtil.writeFile(path, value);
    }

    @Override
    public void setInt(String key, int value) {
        if (key == null) return;
        setString(key, String.valueOf(value));
    }

    @Override
    public void setLong(String key, long value) {
        if (key == null) return;
        setString(key, String.valueOf(value));
    }

    @Override
    public void setFloat(String key, float value) {
        if (key == null) return;
        setString(key, String.valueOf(value));
    }

    @Override
    public void setDouble(String key, double value) {
        if (key == null) return;
        setString(key, String.valueOf(value));
    }

    @Override
    public void setBoolean(String key, boolean value) {
        if (key == null) return;
        setString(key, String.valueOf(value));
    }
}
