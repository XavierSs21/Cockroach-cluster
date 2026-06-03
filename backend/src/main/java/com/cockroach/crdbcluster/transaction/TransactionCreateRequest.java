package com.cockroach.crdbcluster.transaction;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCreateRequest {
    private UUID accountId;
    private BigDecimal amount;
    private String type;
    private String status;
    private String description;
}
