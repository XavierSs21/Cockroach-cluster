package com.cockroach.crdbcluster.audit;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AuditLogResponseDTO {
    private UUID id;
    private UUID userId;
    private String userName;
    private String action;
    private String endpoint;
    private String ipAddress;
    private LocalDateTime createdAt;
}
