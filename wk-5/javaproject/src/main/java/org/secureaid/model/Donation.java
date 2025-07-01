package org.secureaid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Donation {
    private UUID id;
    private String donorName;
    private String encryptedContact;
    private DonationType type;
    private BigDecimal amount; // for cash
    private String itemDescription; // for item
    private Instant timestamp;
    private String integrityHash;

    public enum DonationType {
        CASH, ITEM
    }
} 