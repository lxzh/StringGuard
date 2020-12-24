package com.geetest.stringguard;

/**
 * description Interface of how to encrypt and decrypt a string.
 * author      Created by lxzh
 * date        2020/12/22
 */
public interface IStringGuard {

    /**
     * Encrypt the data by the special key.
     *
     * @param param [0] The original data.
     *              [1] Encrypt key.
     * @return The encrypted data.
     */
    String encrypt(String... param);

    /**
     * Decrypt the data to origin by the special key.
     *
     * @param param [0] The original data.
     *              [1] Encrypt key.
     * @return The original data.
     */
    String decrypt(String... param);

    /**
     * Whether the encrypted string length is over 65535.
     *
     * @param param [0] The original data.
     * @return Ignore this value if the encrypted string is overflow 65535.
     */
    boolean overflow(String param);

    /**
     * Get the encrypt key
     * @return
     */
    String key();
}
