package com.xpel.xlightweight_store_android;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;

import com.tesla.modules.store.Implements.TlSharedPreferences;
import com.tesla.modules.store.Implements.TlUserFiles;
import com.tesla.modules.store.TlStoreManager;
import com.tesla.modules.store.TlStoreProxy;
import com.tesla.modules.store.util.TlStoreUtil;
import com.xpel.xlightweight_store_android.util.ToastUtil;

import java.util.Date;
import java.util.HashMap;

public class MainActivity extends ComponentActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // do what you want
        findViewById(R.id.button_test_anything).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show("Working... do what you want");
                setupData();
            }
        });

        // get baidu.com
        findViewById(R.id.button_test_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show("Working... get baidu.com");
                deleteData();
            }
        });

        String string = null;
        boolean isEquals = "abc".equals(string);
        android.util.Log.i(TAG, "##### isEquals: " + isEquals);

        HashMap<String, Object> spec = new HashMap<>();
        spec.put("aesKey", null);
        String aesKey = String.valueOf(spec.get("aesKey"));

        setupData();
    }

    public void deleteData() {
        // TlSharedPreferences
        {
            String aesKey = null;
            String aesIV = null;
            String moduleName = "com.clear.value";
            TlSharedPreferences sp = new TlSharedPreferences(moduleName, aesKey, aesIV);
            sp.removeKey("YOUR_MAN");
        }

        {
            String aesKey = "12345678901234567890123456789012";
            String aesIV = "abcdef";
            String moduleName = "com.cipher.value";
            TlSharedPreferences sp = new TlSharedPreferences(moduleName, aesKey, aesIV);
            sp.removeKey("YOUR_MAN");
        }

        // TlUserFiles
        {
            String aesKey = null;
            String aesIV = null;
            String moduleName = "SP_NIL";
            TlUserFiles sp = new TlUserFiles(moduleName, aesKey, aesIV);
            sp.removeKey("YOUR_MAN");
        }

        {
            String aesKey = "12345678901234567890123456789012";
            String aesIV = "abcdef";
            String moduleName = "SP_AES";
            TlUserFiles sp = new TlUserFiles(moduleName, aesKey, aesIV);
            sp.removeKey("YOUR_MAN");
        }
    }

    public void setupData() {
        {
            String fileName = "com.tests.value";
            SharedPreferences sp = TlStoreUtil.getContext().getSharedPreferences(fileName, Context.MODE_PRIVATE);
            String value;
            value = sp.getString(null, "---null---key---value---checking---");
            android.util.Log.i(TAG, "SharedPreferences null key value >>: " + value);
            sp.edit().putString(null, "__null_value__❗️").commit();
            value = sp.getString(null, "???????????");
            android.util.Log.i(TAG, "SharedPreferences null key value ##: " + value);
        }

        // TlStoreProxy without AES
        {
            String aesKey = null;
            String aesIV = null;
            String moduleName = "com.store.no.value";
            TlStoreProxy sp = new TlStoreProxy(moduleName, aesKey, aesIV, null);
            sp.setString("YOUR_MAN", new Date() + ": 🐷 You're a good boy 💯");
            String value = sp.getString("YOUR_MAN", "defValue");
            android.util.Log.i(TAG, "##### sp value [NIL]: " + value);

            sp.setString("String", new Date() + ": You're a ✅💄💋🐼🐼🦉🐣");
            sp.setInt("Int", 10086);
            sp.setLong("Long", 9999099988888L);
            sp.setFloat("Float", 100.12300988f);
            sp.setDouble("Double", 8.0090090098d);
            sp.setBoolean("Boolean", true);

            String s = sp.getString("String", "defString");
            int i = sp.getInt("Int", 0);
            long l = sp.getLong("Long", 0L);
            float f = sp.getFloat("Float", 0f);
            double d = sp.getDouble("Double", 0d);
            boolean b = sp.getBoolean("Boolean", false);
            android.util.Log.i(TAG, "@@@[TlStoreProxy][NIL] " + s + ", " + i + ", " + l + ", " + f + ", " + d + ", " + b);
        }

        // TlStoreProxy with AES
        {
            String aesKey = "12345678901234567890123456789012";
            String aesIV = "abcdef";
            String moduleName = "com.store.en.value";
            TlStoreProxy sp = new TlStoreProxy(moduleName, aesKey, aesIV, null);
            sp.setString("YOUR_MAN", new Date() + ": 🐷 You're a good boy 💯");
            String value = sp.getString("YOUR_MAN", "defValue");
            android.util.Log.i(TAG, "##### sp value [AES]: " + value);

            sp.setString("String", new Date() + ": You're a ✅💄💋🐼🐼🦉🐣");
            sp.setInt("Int", 10086);
            sp.setLong("Long", 9999099988888L);
            sp.setFloat("Float", 100.12300988f);
            sp.setDouble("Double", 8.0090090098d);
            sp.setBoolean("Boolean", true);

            String s = sp.getString("String", "defString");
            int i = sp.getInt("Int", 0);
            long l = sp.getLong("Long", 0L);
            float f = sp.getFloat("Float", 0f);
            double d = sp.getDouble("Double", 0d);
            boolean b = sp.getBoolean("Boolean", false);
            android.util.Log.i(TAG, "@@@[TlStoreProxy][AES] " + s + ", " + i + ", " + l + ", " + f + ", " + d + ", " + b);
        }


        // TlSharedPreferences without AES
        {
            String aesKey = null;
            String aesIV = null;
            String moduleName = "com.clear.value";
            TlSharedPreferences sp = new TlSharedPreferences(moduleName, aesKey, aesIV);
            sp.setString("YOUR_MAN", new Date() + ": 🐷 You're a good boy 💯");
            String value = sp.getString("YOUR_MAN", "defValue");
            android.util.Log.i(TAG, "##### sp value [NIL]: " + value);

            sp.setString("String", new Date() + ": You're a ✅💄💋🐼🐼🦉🐣");
            sp.setInt("Int", 10086);
            sp.setLong("Long", 9999099988888L);
            sp.setFloat("Float", 100.12300988f);
            sp.setDouble("Double", 8.0090090098d);
            sp.setBoolean("Boolean", true);

            String s = sp.getString("String", "defString");
            int i = sp.getInt("Int", 0);
            long l = sp.getLong("Long", 0L);
            float f = sp.getFloat("Float", 0f);
            double d = sp.getDouble("Double", 0d);
            boolean b = sp.getBoolean("Boolean", false);
            android.util.Log.i(TAG, "@@@[TlSharedPrf][NIL] " + s + ", " + i + ", " + l + ", " + f + ", " + d + ", " + b);
        }

        // TlSharedPreferences with AES
        {
            String aesKey = "12345678901234567890123456789012";
            String aesIV = "abcdef";
            String moduleName = "com.crypt.value";
            TlSharedPreferences sp = new TlSharedPreferences(moduleName, aesKey, aesIV);
            sp.setString("YOUR_MAN", new Date() + ": 🐷 You're a good boy 💯");
            String value = sp.getString("YOUR_MAN", "defValue");
            android.util.Log.i(TAG, "##### sp value [AES]: " + value);

            sp.setString("String", new Date() + ": You're a ✅💄💋🐼🐼🦉🐣");
            sp.setInt("Int", 10086);
            sp.setLong("Long", 9999099988888L);
            sp.setFloat("Float", 100.12300988f);
            sp.setDouble("Double", 8.0090090098d);
            sp.setBoolean("Boolean", true);

            String s = sp.getString("String", "defString");
            int i = sp.getInt("Int", 0);
            long l = sp.getLong("Long", 0L);
            float f = sp.getFloat("Float", 0f);
            double d = sp.getDouble("Double", 0d);
            boolean b = sp.getBoolean("Boolean", false);
            android.util.Log.i(TAG, "@@@[TlSharedPrf][AES] " + s + ", " + i + ", " + l + ", " + f + ", " + d + ", " + b);
        }

        // TlUserFiles with AES
        {
            String aesKey = null;
            String aesIV = null;
            String moduleName = "SP_NIL";
            TlUserFiles sp = new TlUserFiles(moduleName, aesKey, aesIV);
            sp.setString("YOUR_MAN", new Date() + ": 🐷 You're a good boy 💯");
            String value = sp.getString("YOUR_MAN", "defValue");
            android.util.Log.i(TAG, "##### sp value [NIL]: " + value);

            sp.setString("String", new Date() + ": You're a ✅💄💋🐼🐼🦉🐣");
            sp.setInt("Int", 10086);
            sp.setLong("Long", 9999099988888L);
            sp.setFloat("Float", 100.12300988f);
            sp.setDouble("Double", 8.0090090098d);
            sp.setBoolean("Boolean", true);

            String s = sp.getString("String", "defString");
            int i = sp.getInt("Int", 0);
            long l = sp.getLong("Long", 0L);
            float f = sp.getFloat("Float", 0f);
            double d = sp.getDouble("Double", 0d);
            boolean b = sp.getBoolean("Boolean", false);
            android.util.Log.i(TAG, "@@@[TlUserFiles][NIL] " + s + ", " + i + ", " + l + ", " + f + ", " + d + ", " + b);
        }

        // TlUserFiles with AES
        {
            String aesKey = "12345678901234567890123456789012";
            String aesIV = "abcdef";
            String moduleName = "SP_AES";
            TlUserFiles sp = new TlUserFiles(moduleName, aesKey, aesIV);
            sp.setString("YOUR_MAN", new Date() + ": 🐷 You're a good boy 💯");
            String value = sp.getString("YOUR_MAN", "defValue");
            android.util.Log.i(TAG, "##### sp value [AES]: " + value);

            sp.setString("String", new Date() + ": You're a ✅💄💋🐼🐼🦉🐣");
            sp.setInt("Int", 10086);
            sp.setLong("Long", 9999099988888L);
            sp.setFloat("Float", 100.12300988f);
            sp.setDouble("Double", 8.0090090098d);
            sp.setBoolean("Boolean", true);

            String s = sp.getString("String", "defString");
            int i = sp.getInt("Int", 0);
            long l = sp.getLong("Long", 0L);
            float f = sp.getFloat("Float", 0f);
            double d = sp.getDouble("Double", 0d);
            boolean b = sp.getBoolean("Boolean", false);
            android.util.Log.i(TAG, "@@@[TlUserFiles][AES] " + s + ", " + i + ", " + l + ", " + f + ", " + d + ", " + b);
        }


        // TlStoreManager
        {
            for (int index = 0; index < 2; index++) {
                String aesKey = null;
                String aesIV = null;
                String moduleName = "SP_NIL";
                if (index == 1) {
                    aesKey = "12345678901234567890123456789012";
                    aesIV = "abcdef";
                    moduleName = "SP_AES";
                }
                TlStoreManager.getInstance().register(moduleName, aesKey, aesIV, null, null);
                TlStoreManager sp = TlStoreManager.getInstance();
                sp.setString(moduleName, "YOUR_MAN", new Date() + " 💯 💯 💯 💯 💯 💯");
                String value = sp.getString(moduleName, "YOUR_MAN", "defValue");
                android.util.Log.i(TAG, "===[TlStoreManager][" + index + "]: " + value);

                sp.setString(moduleName, "String", new Date() + ": You're a ✅✅✅✅✅✅");
                sp.setInt(moduleName, "Int", 10086);
                sp.setLong(moduleName, "Long", 9999099988888L);
                sp.setFloat(moduleName, "Float", 100.12300988f);
                sp.setDouble(moduleName, "Double", 8.0090090098d);
                sp.setBoolean(moduleName, "Boolean", true);

                String s = sp.getString(moduleName, "String", "defString");
                int i = sp.getInt(moduleName, "Int", 0);
                long l = sp.getLong(moduleName, "Long", 0L);
                float f = sp.getFloat(moduleName, "Float", 0f);
                double d = sp.getDouble(moduleName, "Double", 0d);
                boolean b = sp.getBoolean(moduleName, "Boolean", false);
                android.util.Log.i(TAG, "===[TlStoreManager][" + index + "]: " + s + ", " + i + ", " + l + ", " + f + ", " + d + ", " + b);
            }
        }
    }
}
