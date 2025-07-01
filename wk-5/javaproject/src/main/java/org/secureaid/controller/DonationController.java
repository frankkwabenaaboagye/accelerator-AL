package org.secureaid.controller;

import jakarta.validation.Valid;
import org.secureaid.dto.DonationRequestDTO;
import org.secureaid.dto.DonationResponseDTO;
import org.secureaid.dto.VerifyResponseDTO;
import org.secureaid.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import java.util.UUID;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/donate")
@Tag(name = "Donations", description = "Endpoints for making and verifying donations")
public class DonationController {
    private final DonationService donationService;

    @Autowired
    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @PostMapping
    @Operation(summary = "Submit a donation", description = "Accepts a donation and returns a summary response.")
    public ResponseEntity<DonationResponseDTO> donate(@Valid @RequestBody DonationRequestDTO request) {
        DonationResponseDTO response = donationService.addDonation(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/donations")
    @Operation(summary = "List all donations", description = "Returns a list of all donations (anonymized, requires authentication).")
    public ResponseEntity<List<DonationResponseDTO>> getDonations() {
        return ResponseEntity.ok(donationService.getAllDonations());
    }

    @GetMapping("/verify/{id}")
    @Operation(summary = "Verify donation integrity", description = "Verifies the integrity of a donation record by ID.")
    public ResponseEntity<VerifyResponseDTO> verifyDonation(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(donationService.verifyDonation(id));
    }
} 