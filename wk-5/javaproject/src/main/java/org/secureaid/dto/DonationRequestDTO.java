package org.secureaid.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DonationRequestDTO {
    @NotBlank
    private String donorName;
    @NotBlank
    private String contact;
    @NotBlank
    private String type; // "CASH" or "ITEM"
    private BigDecimal amount; // required if type is CASH
    private String itemDescription; // required if type is ITEM
} 