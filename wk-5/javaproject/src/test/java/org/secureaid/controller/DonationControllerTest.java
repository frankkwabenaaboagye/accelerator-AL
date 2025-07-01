package org.secureaid.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.secureaid.dto.DonationRequestDTO;
import org.secureaid.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DonationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;

    private String jwtToken;

    @BeforeEach
    void setup() throws Exception {
        // Ensure a user exists and get a JWT
        userService.signup("testuser", "testpass");
        var loginResp = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"testpass\"}"))
                .andReturn().getResponse().getContentAsString();
        jwtToken = objectMapper.readTree(loginResp).get("token").asText();
    }

    @Test
    void testDonateAndVerify() throws Exception {
        // Create a donation
        DonationRequestDTO dto = new DonationRequestDTO();
        dto.setDonorName("Alice");
        dto.setContact("alice@example.com");
        dto.setType("CASH");
        dto.setAmount(new BigDecimal("100.00"));
        String donateResp = mockMvc.perform(post("/donate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.donorName", is("Alice")))
                .andReturn().getResponse().getContentAsString();
        UUID donationId = UUID.fromString(objectMapper.readTree(donateResp).get("id").asText());

        // List donations (requires auth)
        mockMvc.perform(get("/donations").header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(donationId.toString())));

        // Verify donation
        mockMvc.perform(get("/verify/" + donationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid", is(true)));
    }
} 