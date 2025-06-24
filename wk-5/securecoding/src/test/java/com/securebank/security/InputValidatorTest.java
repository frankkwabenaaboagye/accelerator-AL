package com.securebank.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InputValidatorTest {
    @Test
    void testValidUsername() {
        assertTrue(InputValidator.isValidUsername("user_123"));
        assertFalse(InputValidator.isValidUsername("us"));
        assertFalse(InputValidator.isValidUsername("user@name"));
    }

    @Test
    void testSanitizeForHtml() {
        String input = "<script>alert('xss')</script>";
        String sanitized = InputValidator.sanitizeForHtml(input);
        assertFalse(sanitized.contains("<"));
        assertFalse(sanitized.contains(">"));
        assertTrue(sanitized.contains("&lt;script&gt;"));
    }
} 