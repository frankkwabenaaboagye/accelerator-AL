package org.secureaid.service;

import org.secureaid.dto.DonationRequestDTO;
import org.secureaid.dto.DonationResponseDTO;
import org.secureaid.dto.VerifyResponseDTO;
import org.secureaid.model.Donation;
import org.secureaid.model.Donation.DonationType;
import org.springframework.stereotype.Service;
import org.secureaid.util.CryptoUtil;
import org.secureaid.util.HashUtil;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;

@Service
public class DonationService {
    private final ConcurrentHashMap<UUID, Donation> donationStore = new ConcurrentHashMap<>();

    public DonationResponseDTO addDonation(DonationRequestDTO dto) {
        DonationType type = DonationType.valueOf(dto.getType().toUpperCase());
        BigDecimal amount = type == DonationType.CASH ? dto.getAmount() : null;
        String itemDescription = type == DonationType.ITEM ? dto.getItemDescription() : null;
        // Placeholder for encryption and hash
        String encryptedContact = CryptoUtil.encrypt(dto.getContact());
        Instant now = Instant.now();
        String integrityHash = HashUtil.hashDonationData(
                dto.getDonorName(),
                encryptedContact,
                type.name(),
                amount != null ? amount.toPlainString() : null,
                itemDescription,
                now.toString()
        );
        Donation donation = Donation.builder()
                .id(UUID.randomUUID())
                .donorName(dto.getDonorName())
                .encryptedContact(encryptedContact)
                .type(type)
                .amount(amount)
                .itemDescription(itemDescription)
                .timestamp(now)
                .integrityHash(integrityHash)
                .build();
        donationStore.put(donation.getId(), donation);
        return DonationResponseDTO.builder()
                .id(donation.getId())
                .donorName(donation.getDonorName())
                .type(donation.getType().name())
                .amount(donation.getAmount())
                .itemDescription(donation.getItemDescription())
                .timestamp(donation.getTimestamp())
                .build();
    }

    public List<DonationResponseDTO> getAllDonations() {
        return donationStore.values().stream()
                .map(donation -> DonationResponseDTO.builder()
                        .id(donation.getId())
                        .donorName(donation.getDonorName())
                        .type(donation.getType().name())
                        .amount(donation.getAmount())
                        .itemDescription(donation.getItemDescription())
                        .timestamp(donation.getTimestamp())
                        .build())
                .toList();
    }

    public VerifyResponseDTO verifyDonation(UUID id) {
        Donation donation = donationStore.get(id);
        if (donation == null) {
            return VerifyResponseDTO.builder()
                    .id(id)
                    .valid(false)
                    .message("Donation not found")
                    .build();
        }
        String recalculatedHash = HashUtil.hashDonationData(
                donation.getDonorName(),
                donation.getEncryptedContact(),
                donation.getType().name(),
                donation.getAmount() != null ? donation.getAmount().toPlainString() : null,
                donation.getItemDescription(),
                donation.getTimestamp().toString()
        );
        boolean valid = recalculatedHash.equals(donation.getIntegrityHash());
        return VerifyResponseDTO.builder()
                .id(id)
                .valid(valid)
                .message(valid ? "Donation record is valid." : "Donation record has been tampered with!")
                .build();
    }
} 