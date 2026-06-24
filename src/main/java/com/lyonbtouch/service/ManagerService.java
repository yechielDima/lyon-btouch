package com.lyonbtouch.service;

import com.lyonbtouch.exception.BusinessRuleException;
import com.lyonbtouch.exception.ResourceNotFoundException;
import com.lyonbtouch.exception.UnauthorizedException;
import com.lyonbtouch.model.Position;
import com.lyonbtouch.model.User;
import com.lyonbtouch.model.enums.AccountStatus;
import com.lyonbtouch.model.enums.PositionCode;
import com.lyonbtouch.model.enums.SystemRole;
import com.lyonbtouch.repository.PositionRepository;
import com.lyonbtouch.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ManagerService {

    private final UserRepository userRepository;
    private final PositionRepository positionRepository;
    private final AuditService auditService;

    public ManagerService(UserRepository userRepository,
                          PositionRepository positionRepository,
                          AuditService auditService) {
        this.userRepository = userRepository;
        this.positionRepository = positionRepository;
        this.auditService = auditService;
    }

    @Transactional
    public User approveUser(Long managerId, Long userId, SystemRole systemRole,
                            List<PositionCode> qualificationCodes, boolean isChecker) {
        User manager = requireManager(managerId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        if (user.getAccountStatus() == AccountStatus.APPROVED) {
            throw new BusinessRuleException("User is already approved");
        }

        validateCheckerFlag(isChecker, qualificationCodes);

        user.setAccountStatus(AccountStatus.APPROVED);
        user.setSystemRole(systemRole);
        user.setChecker(isChecker);
        user.setQualifications(resolvePositions(qualificationCodes));

        User saved = userRepository.save(user);
        auditService.log(managerId, "USER_APPROVE",
                "Approved user " + user.getFullName() + " with role " + systemRole);
        return saved;
    }

    @Transactional
    public User updateUserProfile(Long managerId, Long userId, SystemRole systemRole,
                                  List<PositionCode> qualificationCodes, boolean isChecker,
                                  Boolean active) {
        User manager = requireManager(managerId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        validateCheckerFlag(isChecker, qualificationCodes);

        if (systemRole != null) {
            user.setSystemRole(systemRole);
        }
        if (qualificationCodes != null) {
            user.setQualifications(resolvePositions(qualificationCodes));
        }
        user.setChecker(isChecker);
        if (active != null) {
            user.setActive(active);
        }

        User saved = userRepository.save(user);
        auditService.log(managerId, "USER_UPDATE",
                "Updated profile for user " + user.getFullName());
        return saved;
    }

    private User requireManager(Long managerId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found: " + managerId));
        if (manager.getSystemRole() != SystemRole.MANAGER) {
            throw new UnauthorizedException("Only managers can perform this action");
        }
        return manager;
    }

    private void validateCheckerFlag(boolean isChecker, List<PositionCode> qualificationCodes) {
        if (isChecker && (qualificationCodes == null || !qualificationCodes.contains(PositionCode.WAITER))) {
            throw new BusinessRuleException("Checker flag requires WAITER qualification");
        }
    }

    private Set<Position> resolvePositions(List<PositionCode> codes) {
        Set<Position> positions = new HashSet<>();
        if (codes != null) {
            for (PositionCode code : codes) {
                Position position = positionRepository.findByPositionCode(code)
                        .orElseThrow(() -> new ResourceNotFoundException("Position not found: " + code));
                positions.add(position);
            }
        }
        return positions;
    }
}
