package com.cockroach.crdbcluster.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<AuditLogResponseDTO>> getAllAuditLogs() {
        return ResponseEntity.ok(auditLogService.getAllAuditLogs()
                .stream()
                .map(auditLogService::toDTO)
                .toList());
    }

   
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLogResponseDTO>> getAuditLogsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(auditLogService.getAuditLogsByUserId(userId)
                .stream()
                .map(auditLogService::toDTO)
                .toList());
    }
}
