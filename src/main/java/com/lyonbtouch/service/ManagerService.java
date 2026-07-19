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
import com.lyonbtouch.sms.SmsSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.lyonbtouch.dto.DtoMapper;
import com.lyonbtouch.dto.UserResponse;

@Service
public class ManagerService {

    private final UserRepository userRepository;
    private final PositionRepository positionRepository;
    private final AuditService auditService;
    private final SmsSender smsSender;

    public ManagerService(UserRepository userRepository,
                          PositionRepository positionRepository,
                          AuditService auditService,
                          SmsSender smsSender) {
        this.userRepository = userRepository;
        this.positionRepository = positionRepository;
        this.auditService = auditService;
        this.smsSender = smsSender;
    }

    @Transactional
    public UserResponse approveUser(Long userId, SystemRole systemRole,
                            List<PositionCode> qualificationCodes, boolean isChecker) {
        Long managerId = com.lyonbtouch.security.SecurityUtils.getCurrentUserId();
        User manager = requireManager(managerId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        if (user.getAccountStatus() == AccountStatus.APPROVED) {
            throw new BusinessRuleException("User is already approved");
        }

        if (qualificationCodes == null) {
            qualificationCodes = new ArrayList<>();
        }

        if (systemRole == SystemRole.SHIFT_MANAGER) {
            if (!qualificationCodes.contains(PositionCode.WAITER)) {
                qualificationCodes.add(PositionCode.WAITER);
            }
            isChecker = true;
        }

        if (isChecker && !qualificationCodes.contains(PositionCode.WAITER)) {
            qualificationCodes.add(PositionCode.WAITER);
        }

        user.setAccountStatus(AccountStatus.APPROVED);
        user.setSystemRole(systemRole);
        user.setChecker(isChecker);
        user.setQualifications(resolvePositions(qualificationCodes));

        User saved = userRepository.save(user);
        auditService.log(managerId, "USER_APPROVE",
                "Approved user " + user.getFullName() + " with role " + systemRole);

        smsSender.send(user.getPhone(),
                "Your Lyon B'Touch account has been approved. Role: " + systemRole);

        return DtoMapper.toUserResponse(saved);
    }

    @Transactional
    public UserResponse updateUserProfile(Long userId, SystemRole systemRole,
                                  List<PositionCode> qualificationCodes, Boolean isChecker,
                                  Boolean active) {
        Long managerId = com.lyonbtouch.security.SecurityUtils.getCurrentUserId();
        User manager = requireManager(managerId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        SystemRole effectiveRole = systemRole != null ? systemRole : user.getSystemRole();
        boolean effectiveChecker = isChecker != null ? isChecker : user.isChecker();

        Set<PositionCode> effectiveQualCodes;
        if (qualificationCodes != null) {
            effectiveQualCodes = new HashSet<>(qualificationCodes);
        } else {
            effectiveQualCodes = new HashSet<>();
            for (Position p : user.getQualifications()) {
                effectiveQualCodes.add(p.getPositionCode());
            }
        }

        if (effectiveRole == SystemRole.SHIFT_MANAGER) {
            effectiveQualCodes.add(PositionCode.WAITER);
            effectiveChecker = true;
        }

        if (effectiveChecker) {
            effectiveQualCodes.add(PositionCode.WAITER);
        }

        if (systemRole != null) {
            user.setSystemRole(systemRole);
        }
        user.setQualifications(resolvePositions(new ArrayList<>(effectiveQualCodes)));
        user.setChecker(effectiveChecker);
        if (active != null) {
            user.setActive(active);
        }

        User saved = userRepository.save(user);
        auditService.log(managerId, "USER_UPDATE",
                "Updated profile for user " + user.getFullName());
        return DtoMapper.toUserResponse(saved);
    }

    private User requireManager(Long managerId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found: " + managerId));
        if (manager.getSystemRole() != SystemRole.MANAGER) {
            throw new UnauthorizedException("Only managers can perform this action");
        }
        return manager;
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
