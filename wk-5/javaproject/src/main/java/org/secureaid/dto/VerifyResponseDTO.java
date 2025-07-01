package org.secureaid.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class VerifyResponseDTO {
    private UUID id;
    private boolean valid;
    private String message;
} 