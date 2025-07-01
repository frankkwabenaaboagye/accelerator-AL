package org.secureaid.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HashUtilTest {
    @Test
    void testHashDonationData() {
        String hash1 = HashUtil.hashDonationData("Alice", "contact", "CASH", "100", null, "2024-07-01T12:00:00Z");
        String hash2 = HashUtil.hashDonationData("Alice", "contact", "CASH", "100", null, "2024-07-01T12:00:00Z");
        String hash3 = HashUtil.hashDonationData("Bob", "contact", "CASH", "100", null, "2024-07-01T12:00:00Z");
        assertEquals(hash1, hash2);
        assertNotEquals(hash1, hash3);
    }
} 