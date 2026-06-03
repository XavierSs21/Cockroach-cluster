package com.cockroach.crdbcluster.transaction;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TransactionResponseDTO {
    private UUID id;
    private UUID accountId;
    private BigDecimal amount;
    private String type;
    private String status;
    private String description;
    private LocalDateTime createdAt;
}
