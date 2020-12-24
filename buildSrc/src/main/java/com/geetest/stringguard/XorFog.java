package com.geetest.stringguard;

/**
 * 自定义算法实现，此文件存储目录路径须和其包名一致
 *
 * @author Sundy
 * @since 2019/3/4 23:41
 */
public class XorFog implements IStringGuard {

    @Override
    public String encrypt(String... param) {
        String newData = new String(xor(param[0].getBytes(), param[1].getBytes()));
        return newData;
    }

    @Override
    public String decrypt(String... param) {
        String newData = new String(xor(param[0].getBytes(), param[1].getBytes()));
        return newData;
    }

    @Override
    public boolean overflow(String param) {
        return param != null && param.length() * 4 / 3 >= 65535;
    }

    @Override
    public String key() {
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
