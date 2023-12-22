package com.tesla.modules.store;

public interface TlStoreInterface {

    boolean contains(String key);

    void removeKey(String key);

    String getString(String key, String defValue);

    int getInt(String key, int defValue);

    long getLong(String key, long defValue);

    float getFloat(String key, float defValue);

    double getDouble(String key, double defValue);

    boolean getBoolean(String key, boolean defValue);

    void setString(String key, String value);
    
    void setInt(String key, int value);
    
    void setLong(String key, long value);
    
    void setFloat(String key, float value);

    void setDouble(String key, double value);

    void setBoolean(String key, boolean value);
}
