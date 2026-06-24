package com.lyonbtouch.service;

import com.lyonbtouch.model.AuditLog;
import com.lyonbtouch.model.User;
import com.lyonbtouch.repository.AuditLogRepository;
import com.lyonbtouch.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditService(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public AuditLog log(Long actorUserId, String actionType, String description) {
        AuditLog auditLog = new AuditLog();
        if (actorUserId != null) {
            User actor = userRepository.findById(actorUserId).orElse(null);
            auditLog.setActorUser(actor);
        }
        auditLog.setActionType(actionType);
        auditLog.setDescription(description);
        return auditLogRepository.save(auditLog);
    }
}
