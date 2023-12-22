package com.tesla.modules.store.Implements;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tesla.modules.store.util.ByteUtil;

import java.util.function.Function;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public abstract class TlStoreBase {

    public String moduleName;
    public String aesKey;
    public String aesIV;

    // key transformer
    public Function<String, String> keyTransformer;

    // value transformer
    public Function<String, String> valueEncoder;
    public Function<String, String> valueDecoder;

    public TlStoreBase(@NonNull String moduleName, @Nullable String aesKey, @Nullable String aesIV) {
        this.moduleName = moduleName;
        // fix length
        String filling = "00000000000000000000000000000000";
        if (aesKey != null && !aesKey.isEmpty() && aesKey.length() != 32) {
            aesKey = (aesKey + filling).substring(0, 32);
        }
        if (aesIV != null && !aesIV.isEmpty() && aesIV.length() != 16) {
            aesIV = (aesIV + filling).substring(0, 16);
        }
        this.aesKey = aesKey;
        this.aesIV = aesIV;

        if (isEnableCrypto()) {
            // key transformer
            keyTransformer = (String key) -> {
                String result = encryptKeyIfNeeded(key);
                if (result != null) return result;
                return key;
            };
            valueEncoder = (String value) -> {
                String result = encryptValueIfNeeded(value);
                if (result != null) return result;
                return value;
            };
            valueDecoder = (String value) -> {
                String result = decryptValueIfNeeded(value);
                if (result != null) return result;
                return value;
            };
        }
    }

    public boolean isEnableCrypto() {
        return aesKey != null && aesIV != null;
    }

    protected String encodeKeyIfNeeded(String key) {
        if (this.keyTransformer != null && key != null) {
            key = this.keyTransformer.apply(key);
        }
        return key;
    }

    /**
     * @return Important!!! return null if crypto is disabled or encrypt failed
     */
    private @Nullable String encryptKeyIfNeeded(String key) {
        if (!isEnableCrypto()) return null;
        if (key == null) return null;
        try {
            Cipher cipher = newCipherInstance(Cipher.ENCRYPT_MODE);
            return ByteUtil.base64Encodes(cipher.doFinal(ByteUtil.bytes(key)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return Important!!! return null if crypto is disabled or encrypt failed
     */
    private @Nullable <T> String encryptValueIfNeeded(T value) {
        if (!isEnableCrypto()) return null;
        if (value == null) return null;
        try {
            String string = String.valueOf(value);
            Cipher cipher = newCipherInstance(Cipher.ENCRYPT_MODE);
            return ByteUtil.base64Encodes(cipher.doFinal(ByteUtil.bytes(string)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return Important!!! return null if crypto is disabled or encrypt failed
     */
    private String decryptValueIfNeeded(String value) {
        if (!isEnableCrypto()) return null;
        if (value == null) return null;
        try {
            Cipher cipher = newCipherInstance(Cipher.DECRYPT_MODE);
            return ByteUtil.strings(cipher.doFinal(ByteUtil.base64Decodes(value)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * AES key, iv, cipher
     */
    private SecretKeySpec _key;

    private SecretKeySpec getAesKey() {
        assert aesKey != null;
        if (_key == null) {
            _key = new SecretKeySpec(ByteUtil.bytes(aesKey), "AES");
        }
        return _key;
    }

    private IvParameterSpec _iv;

    private IvParameterSpec getAesIV() {
        assert aesIV != null;
        if (_iv == null) {
            _iv = new IvParameterSpec(ByteUtil.bytes(aesIV));
        }
        return _iv;
    }

    private Cipher newCipherInstance(int mode) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // Cipher.ENCRYPT_MODE, Cipher.DECRYPT_MODE
        cipher.init(mode, getAesKey(), getAesIV());
        return cipher;
    }
}
