package io.noties.markwon.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class AES128GCMUtilsTest {

    // 16-byte key for AES-128
    private static final byte[] TEST_KEY = "0123456789abcdef".getBytes(StandardCharsets.UTF_8);

    @Test
    public void encrypt_decrypt_roundtrip() throws GeneralSecurityException {
        String originalText = "Hello, AES-128-GCM!";
        byte[] plaintext = originalText.getBytes(StandardCharsets.UTF_8);

        byte[] encrypted = AES128GCMUtils.encrypt(plaintext, TEST_KEY);
        byte[] decrypted = AES128GCMUtils.decrypt(encrypted, TEST_KEY);

        assertArrayEquals(plaintext, decrypted);
        assertEquals(originalText, new String(decrypted, StandardCharsets.UTF_8));
    }

    @Test
    public void encrypt_decrypt_empty_plaintext() throws GeneralSecurityException {
        byte[] plaintext = new byte[0];

        byte[] encrypted = AES128GCMUtils.encrypt(plaintext, TEST_KEY);
        byte[] decrypted = AES128GCMUtils.decrypt(encrypted, TEST_KEY);

        assertArrayEquals(plaintext, decrypted);
    }

    @Test
    public void encrypt_decrypt_large_data() throws GeneralSecurityException {
        byte[] plaintext = new byte[10000];
        Arrays.fill(plaintext, (byte) 'A');

        byte[] encrypted = AES128GCMUtils.encrypt(plaintext, TEST_KEY);
        byte[] decrypted = AES128GCMUtils.decrypt(encrypted, TEST_KEY);

        assertArrayEquals(plaintext, decrypted);
    }

    @Test
    public void encrypt_produces_different_ciphertext() throws GeneralSecurityException {
        byte[] plaintext = "Same plaintext".getBytes(StandardCharsets.UTF_8);

        // Due to random IV, encrypting the same plaintext should produce different ciphertext
        byte[] encrypted1 = AES128GCMUtils.encrypt(plaintext, TEST_KEY);
        byte[] encrypted2 = AES128GCMUtils.encrypt(plaintext, TEST_KEY);

        assertFalse(Arrays.equals(encrypted1, encrypted2));
    }

    @Test
    public void encrypt_ciphertext_longer_than_plaintext() throws GeneralSecurityException {
        byte[] plaintext = "Short".getBytes(StandardCharsets.UTF_8);

        byte[] encrypted = AES128GCMUtils.encrypt(plaintext, TEST_KEY);

        // Ciphertext should include 12-byte IV + plaintext + 16-byte auth tag
        assertEquals(plaintext.length + 12 + 16, encrypted.length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void encrypt_invalid_key_length_short() throws GeneralSecurityException {
        byte[] plaintext = "Test".getBytes(StandardCharsets.UTF_8);
        byte[] shortKey = "short".getBytes(StandardCharsets.UTF_8);

        AES128GCMUtils.encrypt(plaintext, shortKey);
    }

    @Test(expected = IllegalArgumentException.class)
    public void encrypt_invalid_key_length_long() throws GeneralSecurityException {
        byte[] plaintext = "Test".getBytes(StandardCharsets.UTF_8);
        byte[] longKey = "this key is way too long for aes128".getBytes(StandardCharsets.UTF_8);

        AES128GCMUtils.encrypt(plaintext, longKey);
    }

    @Test(expected = IllegalArgumentException.class)
    public void decrypt_invalid_key_length() throws GeneralSecurityException {
        byte[] ciphertext = new byte[30];
        byte[] shortKey = "short".getBytes(StandardCharsets.UTF_8);

        AES128GCMUtils.decrypt(ciphertext, shortKey);
    }

    @Test(expected = IllegalArgumentException.class)
    public void decrypt_ciphertext_too_short() throws GeneralSecurityException {
        byte[] shortCiphertext = new byte[5];

        AES128GCMUtils.decrypt(shortCiphertext, TEST_KEY);
    }

    @Test
    public void decrypt_wrong_key_fails() throws GeneralSecurityException {
        byte[] plaintext = "Secret data".getBytes(StandardCharsets.UTF_8);
        byte[] wrongKey = "fedcba9876543210".getBytes(StandardCharsets.UTF_8);

        byte[] encrypted = AES128GCMUtils.encrypt(plaintext, TEST_KEY);

        try {
            AES128GCMUtils.decrypt(encrypted, wrongKey);
            fail("Should have thrown exception due to authentication failure");
        } catch (GeneralSecurityException e) {
            // Expected - authentication tag validation failed
        }
    }

    @Test
    public void decrypt_tampered_ciphertext_fails() throws GeneralSecurityException {
        byte[] plaintext = "Original data".getBytes(StandardCharsets.UTF_8);

        byte[] encrypted = AES128GCMUtils.encrypt(plaintext, TEST_KEY);
        
        // Tamper with the ciphertext
        encrypted[encrypted.length - 1] ^= 0xFF;

        try {
            AES128GCMUtils.decrypt(encrypted, TEST_KEY);
            fail("Should have thrown exception due to tampered ciphertext");
        } catch (GeneralSecurityException e) {
            // Expected - authentication tag validation failed
        }
    }

    @Test
    public void bytesToHex_basic() {
        byte[] bytes = {0x00, 0x01, 0x0f, (byte) 0xff};
        String hex = AES128GCMUtils.bytesToHex(bytes);
        assertEquals("00010fff", hex);
    }

    @Test
    public void bytesToHex_empty() {
        byte[] bytes = new byte[0];
        String hex = AES128GCMUtils.bytesToHex(bytes);
        assertEquals("", hex);
    }

    @Test
    public void hexToBytes_basic() {
        String hex = "00010fff";
        byte[] bytes = AES128GCMUtils.hexToBytes(hex);
        assertArrayEquals(new byte[]{0x00, 0x01, 0x0f, (byte) 0xff}, bytes);
    }

    @Test
    public void hexToBytes_empty() {
        String hex = "";
        byte[] bytes = AES128GCMUtils.hexToBytes(hex);
        assertArrayEquals(new byte[0], bytes);
    }

    @Test
    public void hexToBytes_uppercase() {
        String hex = "ABCDEF";
        byte[] bytes = AES128GCMUtils.hexToBytes(hex);
        assertArrayEquals(new byte[]{(byte) 0xab, (byte) 0xcd, (byte) 0xef}, bytes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void hexToBytes_odd_length() {
        AES128GCMUtils.hexToBytes("abc");
    }

    @Test
    public void hex_roundtrip() {
        byte[] original = {0x12, 0x34, 0x56, 0x78, (byte) 0x9a, (byte) 0xbc, (byte) 0xde, (byte) 0xf0};
        String hex = AES128GCMUtils.bytesToHex(original);
        byte[] result = AES128GCMUtils.hexToBytes(hex);
        assertArrayEquals(original, result);
    }

    @Test
    public void encrypt_decrypt_binary_data() throws GeneralSecurityException {
        // Test with all possible byte values
        byte[] plaintext = new byte[256];
        for (int i = 0; i < 256; i++) {
            plaintext[i] = (byte) i;
        }

        byte[] encrypted = AES128GCMUtils.encrypt(plaintext, TEST_KEY);
        byte[] decrypted = AES128GCMUtils.decrypt(encrypted, TEST_KEY);

        assertArrayEquals(plaintext, decrypted);
    }
}
