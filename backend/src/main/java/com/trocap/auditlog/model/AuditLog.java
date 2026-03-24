package com.trocap.auditlog.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    private String id;

    @Indexed
    private String username;

    private String action;
    private String entityType;
    private String entityId;
    private String details;
    private String ipAddress;

    @CreatedDate
    private LocalDateTime createdAt;
}
