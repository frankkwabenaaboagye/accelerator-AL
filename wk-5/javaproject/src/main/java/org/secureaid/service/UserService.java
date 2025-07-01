package org.secureaid.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {
    private final ConcurrentHashMap<String, String> userStore = new ConcurrentHashMap<>();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void signup(String username, String password) {
        if (userStore.containsKey(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        userStore.put(username, passwordEncoder.encode(password));
    }

    public boolean validateLogin(String username, String password) {
        String hashed = userStore.get(username);
        return hashed != null && passwordEncoder.matches(password, hashed);
    }
} 