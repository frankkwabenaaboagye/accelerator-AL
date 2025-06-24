package com.securebank.crypto;

import org.junit.jupiter.api.Test;
import javax.crypto.SecretKey;
import java.security.SecureRandom;
import static org.junit.jupiter.api.Assertions.*;

public class CryptoUtilsTest {
    @Test
    void testAesEncryptionDecryption() throws Exception {
        SecretKey key = CryptoUtils.generateAESKey();
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        String original = "Sensitive bank data";
        String encrypted = CryptoUtils.encrypt(original, key, iv);
        String decrypted = CryptoUtils.decrypt(encrypted, key, iv);
        assertEquals(original, decrypted);
    }
} 