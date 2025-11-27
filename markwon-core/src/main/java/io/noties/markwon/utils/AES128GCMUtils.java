package io.noties.markwon.utils;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utility class for AES-128-GCM encryption and decryption.
 * <p>
 * AES-128-GCM provides authenticated encryption with associated data (AEAD),
 * which provides confidentiality, integrity, and authenticity assurances.
 *
 * @since 4.7.0
 */
public abstract class AES128GCMUtils {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12; // 96 bits
    private static final int GCM_TAG_LENGTH = 128; // 128 bits

    /**
     * Encrypts the given plaintext using AES-128-GCM.
     * <p>
     * The result includes the IV prepended to the ciphertext.
     *
     * @param plaintext the plaintext to encrypt
     * @param key       the 16-byte (128-bit) encryption key
     * @return byte array containing IV + ciphertext + authentication tag
     * @throws GeneralSecurityException if encryption fails
     * @throws IllegalArgumentException if key length is not 16 bytes
     */
    @NonNull
    public static byte[] encrypt(@NonNull byte[] plaintext, @NonNull byte[] key)
            throws GeneralSecurityException {
        validateKey(key);

        // Generate a random IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, parameterSpec);
        byte[] ciphertext = cipher.doFinal(plaintext);

        // Combine IV and ciphertext
        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + ciphertext.length);
        byteBuffer.put(iv);
        byteBuffer.put(ciphertext);

        return byteBuffer.array();
    }

    /**
     * Decrypts the given ciphertext using AES-128-GCM.
     * <p>
     * Expects the input to have the IV prepended to the ciphertext.
     *
     * @param ciphertext the ciphertext to decrypt (IV + ciphertext + authentication tag)
     * @param key        the 16-byte (128-bit) decryption key
     * @return the decrypted plaintext
     * @throws GeneralSecurityException if decryption fails or authentication tag is invalid
     * @throws IllegalArgumentException if key length is not 16 bytes or ciphertext is too short
     */
    @NonNull
    public static byte[] decrypt(@NonNull byte[] ciphertext, @NonNull byte[] key)
            throws GeneralSecurityException {
        validateKey(key);

        if (ciphertext.length < GCM_IV_LENGTH) {
            throw new IllegalArgumentException(
                    "Ciphertext too short, must be at least " + GCM_IV_LENGTH + " bytes");
        }

        // Extract IV from the beginning
        ByteBuffer byteBuffer = ByteBuffer.wrap(ciphertext);
        byte[] iv = new byte[GCM_IV_LENGTH];
        byteBuffer.get(iv);

        // Extract remaining ciphertext
        byte[] encryptedData = new byte[byteBuffer.remaining()];
        byteBuffer.get(encryptedData);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);

        cipher.init(Cipher.DECRYPT_MODE, keySpec, parameterSpec);

        return cipher.doFinal(encryptedData);
    }

    /**
     * Converts a byte array to a hexadecimal string.
     *
     * @param bytes the byte array to convert
     * @return the hexadecimal string representation
     */
    @NonNull
    public static String bytesToHex(@NonNull byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Converts a hexadecimal string to a byte array.
     *
     * @param hex the hexadecimal string to convert
     * @return the byte array
     * @throws IllegalArgumentException if the hex string is invalid
     */
    @NonNull
    public static byte[] hexToBytes(@NonNull String hex) {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex string must have even length");
        }

        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int index = i * 2;
            int value = Integer.parseInt(hex.substring(index, index + 2), 16);
            bytes[i] = (byte) value;
        }
        return bytes;
    }

    private static void validateKey(@NonNull byte[] key) {
        if (key.length != 16) {
            throw new IllegalArgumentException(
                    "Key must be 16 bytes (128 bits), but was " + key.length + " bytes");
        }
    }

    private AES128GCMUtils() {
    }
}
