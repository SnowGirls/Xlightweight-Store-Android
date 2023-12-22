package com.tesla.modules.store.util;

import android.util.Base64;

import androidx.annotation.NonNull;

import java.nio.charset.StandardCharsets;

public class ByteUtil {

    public static byte[] bytes(@NonNull String data) {
        return data.getBytes(StandardCharsets.UTF_8);
    }

    public static String strings(@NonNull byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }

    public static byte[] base64Encode(@NonNull byte[] data) {
        return Base64.encode(data, Base64.NO_WRAP);
    }

    public static byte[] base64Decode(@NonNull byte[] data) {
        return Base64.decode(data, Base64.NO_WRAP);
    }

    public static String base64Encodes(@NonNull byte[] data) {
        return strings(base64Encode(data));
    }

    public static byte[] base64Decodes(@NonNull String data) {
        return base64Decode(bytes(data));
    }

}
