package org.secureaid.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class DonationResponseDTO {
    private UUID id;
    private String donorName;
    private String type;
    private BigDecimal amount;
    private String itemDescription;
    private Instant timestamp;
} 