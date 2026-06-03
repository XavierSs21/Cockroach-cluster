package com.cockroach.crdbcluster.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Obtener todos los registros de auditoría
     */
    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAll();
    }

    /**
     * Obtener registros de auditoría de un usuario específico
     */
    public List<AuditLog> getAuditLogsByUserId(UUID userId) {
        return auditLogRepository.findByUserId(userId);
    }

    /**
     * Crear un nuevo registro de auditoría
     */
    public AuditLog createAuditLog(AuditLog auditLog) {
        return auditLogRepository.save(auditLog);
    }

    public AuditLogResponseDTO toDTO(AuditLog auditLog) {
        String userName = auditLog.getUser() != null ? auditLog.getUser().getName() : "ANONYMOUS";
        return AuditLogResponseDTO.builder()
                .id(auditLog.getId())
                .userId(auditLog.getUser() != null ? auditLog.getUser().getId() : null)
                .userName(userName)
                .action(auditLog.getAction())
                .endpoint(auditLog.getEndpoint())
                .ipAddress(auditLog.getIpAddress())
                .createdAt(auditLog.getCreatedAt())
                .build();
    }
}
