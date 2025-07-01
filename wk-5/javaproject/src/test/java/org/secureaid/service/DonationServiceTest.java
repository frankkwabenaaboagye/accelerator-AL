package org.secureaid.service;

import org.junit.jupiter.api.Test;
import org.secureaid.dto.DonationRequestDTO;
import org.secureaid.dto.DonationResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DonationServiceTest {

    @Autowired
    private DonationService donationService;

    @Test
    public void testAddAndGetAllDonations() {
        // Add a donation
        DonationRequestDTO dto = new DonationRequestDTO();
        dto.setDonorName("Test Donor");
        dto.setContact("test@donor.com");
        dto.setType("CASH");
        dto.setAmount(new BigDecimal("50.00"));
        DonationResponseDTO response = donationService.addDonation(dto);

        // Get all donations and check that the new one is present
        DonationResponseDTO stored = donationService.getAllDonations().stream()
                .filter(d -> d.getId().equals(response.getId()))
                .findFirst().orElse(null);
        assertNotNull(stored);
        assertEquals("Test Donor", stored.getDonorName());
        assertEquals("CASH", stored.getType());
        assertEquals(new BigDecimal("50.00"), stored.getAmount());
    }
}