package org.secureaid.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CryptoUtilTest {
    @Test
    void testEncryptDecrypt() {
        String original = "test@example.com";
        String encrypted = CryptoUtil.encrypt(original);
        assertNotEquals(original, encrypted);
        String decrypted = CryptoUtil.decrypt(encrypted);
        assertEquals(original, decrypted);
    }
} 