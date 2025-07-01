package org.secureaid.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class HashUtil {
    private static final String SECRET_SALT = "MyHashSalt";

    public static String hashDonationData(String... fields) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            StringBuilder sb = new StringBuilder(SECRET_SALT);
            for (String field : fields) {
                sb.append(":").append(field == null ? "" : field);
            }
            byte[] hash = digest.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Hashing error", e);
        }
    }
} 