package org.secureaid.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {
    @Test
    void testGenerateAndValidateToken() {
        String username = "admin";
        String token = JwtUtil.generateToken(username);
        assertTrue(JwtUtil.validateToken(token));
        assertEquals(username, JwtUtil.getUsername(token));
    }

    @Test
    void testInvalidToken() {
        assertFalse(JwtUtil.validateToken("invalid.token.value"));
    }
} 