package com.geetest.stringguard;

public class XorFog {

    public String encrypt(String... param) {
        String newData = new String(xor(param[0].getBytes(), key().getBytes()));
        return newData;
    }

    public String decrypt(String... param) {
        String newData = new String(xor(param[0].getBytes(), key().getBytes()));
        return newData;
    }

    public boolean overflow(String param) {
        return param != null && param.length() >= 65535;
    }

    String key() {
        return "Gt123456";
    }

    private static byte[] xor(byte[] data, byte[] key) {
        int len = data.length;
        int lenKey = key.length;
        int i = 0;
        int j = 0;
        while (i < len) {
            if (j >= lenKey) {
                j = 0;
            }
            data[i] = (byte) (data[i] ^ key[j]);
            i++;
            j++;
        }
        return data;
    }

}
