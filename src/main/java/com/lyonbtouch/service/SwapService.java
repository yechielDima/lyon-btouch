package com.lyonbtouch.service;

import com.lyonbtouch.exception.BusinessRuleException;
import com.lyonbtouch.exception.ResourceNotFoundException;
import com.lyonbtouch.exception.UnauthorizedException;
import com.lyonbtouch.model.*;
import com.lyonbtouch.model.enums.SwapStatus;
import com.lyonbtouch.model.enums.SystemRole;
import com.lyonbtouch.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SwapService {

    private final SwapRequestRepository swapRequestRepository;
    private final ScheduleEntryRepository scheduleEntryRepository;
    private final UserRepository userRepository;
    private final ReliabilityService reliabilityService;
    private final AuditService auditService;

    public SwapService(SwapRequestRepository swapRequestRepository,
                       ScheduleEntryRepository scheduleEntryRepository,
                       UserRepository userRepository,
                       ReliabilityService reliabilityService,
                       AuditService auditService) {
        this.swapRequestRepository = swapRequestRepository;
        this.scheduleEntryRepository = scheduleEntryRepository;
        this.userRepository = userRepository;
        this.reliabilityService = reliabilityService;
        this.auditService = auditService;
    }

    @Transactional
    public SwapRequest openForSwap(Long userId, Long entryId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        ScheduleEntry entry = scheduleEntryRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException("ScheduleEntry not found: " + entryId));

        if (!entry.getUser().getId().equals(userId)) {
            throw new BusinessRuleException("Only the assigned user can open their shift for swap");
        }

        List<SwapRequest> existing = swapRequestRepository.findByScheduleEntry(entry);
        boolean hasActiveSwap = existing.stream()
                .anyMatch(sr -> sr.getStatus() == SwapStatus.OPEN || sr.getStatus() == SwapStatus.PENDING_APPROVAL);
        if (hasActiveSwap) {
            throw new BusinessRuleException("An active swap request already exists for this entry");
        }

        SwapRequest swap = new SwapRequest();
        swap.setScheduleEntry(entry);
        swap.setRequestingUser(user);
        swap.setStatus(SwapStatus.OPEN);

        SwapRequest saved = swapRequestRepository.save(swap);
        auditService.log(userId, "SWAP_OPEN",
                "Opened shift entry " + entryId + " for swap");
        return saved;
    }

    @Transactional
    public SwapRequest offerToCover(Long userId, Long swapRequestId) {
        User coveringUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        SwapRequest swap = swapRequestRepository.findById(swapRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("SwapRequest not found: " + swapRequestId));

        if (swap.getStatus() != SwapStatus.OPEN) {
            throw new BusinessRuleException("Swap request is not open for offers");
        }

        if (swap.getRequestingUser().getId().equals(userId)) {
            throw new BusinessRuleException("Cannot cover your own swap request");
        }

        Position requiredPosition = swap.getScheduleEntry().getPosition();
        if (!coveringUser.getQualifications().contains(requiredPosition)) {
            throw new BusinessRuleException("User is not qualified for position: " + requiredPosition.getPositionCode());
        }

        swap.setCoveringUser(coveringUser);
        swap.setStatus(SwapStatus.PENDING_APPROVAL);

        SwapRequest saved = swapRequestRepository.save(swap);
        auditService.log(userId, "SWAP_OFFER",
                "Offered to cover swap request " + swapRequestId);
        return saved;
    }

    @Transactional
    public SwapRequest approveSwap(Long approverId, Long swapRequestId) {
        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + approverId));
        SwapRequest swap = swapRequestRepository.findById(swapRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("SwapRequest not found: " + swapRequestId));

        if (swap.getStatus() != SwapStatus.PENDING_APPROVAL) {
            throw new BusinessRuleException("Swap request is not pending approval");
        }

        validateApproverAuthority(approver, swap);

        ScheduleEntry entry = swap.getScheduleEntry();
        entry.setUser(swap.getCoveringUser());
        entry.setAssignmentSource(com.lyonbtouch.model.enums.AssignmentSource.MANUAL);
        entry.setAssignedBy(approver);
        scheduleEntryRepository.save(entry);

        swap.setStatus(SwapStatus.APPROVED);
        swap.setApprovedBy(approver);
        SwapRequest saved = swapRequestRepository.save(swap);

        reliabilityService.addEvent(swap.getCoveringUser().getId(), 5,
                "Covered swap request " + swapRequestId, swapRequestId);

        auditService.log(approverId, "SWAP_APPROVE",
                "Approved swap request " + swapRequestId);
        return saved;
    }

    @Transactional
    public SwapRequest rejectSwap(Long approverId, Long swapRequestId) {
        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + approverId));
        SwapRequest swap = swapRequestRepository.findById(swapRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("SwapRequest not found: " + swapRequestId));

        if (swap.getStatus() != SwapStatus.PENDING_APPROVAL) {
            throw new BusinessRuleException("Swap request is not pending approval");
        }

        validateApproverAuthority(approver, swap);

        swap.setStatus(SwapStatus.REJECTED);
        swap.setCoveringUser(null);
        SwapRequest saved = swapRequestRepository.save(swap);

        auditService.log(approverId, "SWAP_REJECT",
                "Rejected swap request " + swapRequestId);
        return saved;
    }

    @Transactional
    public SwapRequest cancelSwap(Long userId, Long swapRequestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        SwapRequest swap = swapRequestRepository.findById(swapRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("SwapRequest not found: " + swapRequestId));

        if (!swap.getRequestingUser().getId().equals(userId)) {
            throw new BusinessRuleException("Only the requesting user can cancel their swap request");
        }

        if (swap.getStatus() == SwapStatus.APPROVED || swap.getStatus() == SwapStatus.CANCELLED) {
            throw new BusinessRuleException("Cannot cancel a swap that is already " + swap.getStatus());
        }

        swap.setStatus(SwapStatus.CANCELLED);
        SwapRequest saved = swapRequestRepository.save(swap);

        auditService.log(userId, "SWAP_CANCEL",
                "Cancelled swap request " + swapRequestId);
        return saved;
    }

    public List<SwapRequest> getSwapsByStatus(SwapStatus status) {
        return swapRequestRepository.findByStatus(status);
    }

    public List<SwapRequest> getSwapsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        return swapRequestRepository.findByRequestingUser(user);
    }

    public List<SwapRequest> getAllSwaps() {
        return swapRequestRepository.findAll();
    }

    private void validateApproverAuthority(User approver, SwapRequest swap) {
        if (approver.getSystemRole() == SystemRole.MANAGER) {
            return;
        }

        if (approver.getSystemRole() == SystemRole.SHIFT_MANAGER) {
            Shift shift = swap.getScheduleEntry().getShift();
            if (shift.getShiftManager() != null && shift.getShiftManager().getId().equals(approver.getId())) {
                return;
            }
        }

        throw new UnauthorizedException("User does not have authority to approve/reject this swap");
    }
}
