package com.lyonbtouch.service;

import com.lyonbtouch.exception.BusinessRuleException;
import com.lyonbtouch.exception.ResourceNotFoundException;
import com.lyonbtouch.exception.UnauthorizedException;
import com.lyonbtouch.model.*;
import com.lyonbtouch.model.enums.*;
import com.lyonbtouch.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleWeekRepository scheduleWeekRepository;
    private final ShiftRepository shiftRepository;
    private final ShiftRequirementRepository shiftRequirementRepository;
    private final ScheduleEntryRepository scheduleEntryRepository;
    private final UserRepository userRepository;
    private final PositionRepository positionRepository;
    private final AvailabilityRepository availabilityRepository;
    private final AuditService auditService;

    public ScheduleService(ScheduleWeekRepository scheduleWeekRepository,
                           ShiftRepository shiftRepository,
                           ShiftRequirementRepository shiftRequirementRepository,
                           ScheduleEntryRepository scheduleEntryRepository,
                           UserRepository userRepository,
                           PositionRepository positionRepository,
                           AvailabilityRepository availabilityRepository,
                           AuditService auditService) {
        this.scheduleWeekRepository = scheduleWeekRepository;
        this.shiftRepository = shiftRepository;
        this.shiftRequirementRepository = shiftRequirementRepository;
        this.scheduleEntryRepository = scheduleEntryRepository;
        this.userRepository = userRepository;
        this.positionRepository = positionRepository;
        this.availabilityRepository = availabilityRepository;
        this.auditService = auditService;
    }

    @Transactional
    public ScheduleWeek createWeek(Long managerId, LocalDate weekStartDate) {
        User manager = requireManager(managerId);

        if (scheduleWeekRepository.findByWeekStartDate(weekStartDate).isPresent()) {
            throw new BusinessRuleException("Schedule week already exists for date: " + weekStartDate);
        }

        ScheduleWeek week = new ScheduleWeek();
        week.setWeekStartDate(weekStartDate);
        week.setStatus(WeekStatus.DRAFT);

        ScheduleWeek saved = scheduleWeekRepository.save(week);
        auditService.log(managerId, "WEEK_CREATE", "Created schedule week starting " + weekStartDate);
        return saved;
    }

    public ScheduleWeek getWeek(Long weekId) {
        return scheduleWeekRepository.findById(weekId)
                .orElseThrow(() -> new ResourceNotFoundException("ScheduleWeek not found: " + weekId));
    }

    public List<ScheduleWeek> getAllWeeks() {
        return scheduleWeekRepository.findAll();
    }

    @Transactional
    public Shift addShift(Long managerId, Long weekId, LocalDate shiftDate, ShiftType shiftType) {
        User manager = requireManager(managerId);
        ScheduleWeek week = scheduleWeekRepository.findById(weekId)
                .orElseThrow(() -> new ResourceNotFoundException("ScheduleWeek not found: " + weekId));

        if (week.getStatus() == WeekStatus.PUBLISHED) {
            throw new BusinessRuleException("Cannot add shifts to a published week");
        }

        if (shiftRepository.findByShiftDateAndShiftType(shiftDate, shiftType).isPresent()) {
            throw new BusinessRuleException("Shift already exists for " + shiftDate + " " + shiftType);
        }

        Shift shift = new Shift();
        shift.setWeek(week);
        shift.setShiftDate(shiftDate);
        shift.setShiftType(shiftType);

        Shift saved = shiftRepository.save(shift);
        auditService.log(managerId, "SHIFT_CREATE",
                "Created " + shiftType + " shift on " + shiftDate);
        return saved;
    }

    public Shift getShift(Long shiftId) {
        return shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found: " + shiftId));
    }

    public List<Shift> getShiftsForWeek(Long weekId) {
        ScheduleWeek week = scheduleWeekRepository.findById(weekId)
                .orElseThrow(() -> new ResourceNotFoundException("ScheduleWeek not found: " + weekId));
        return shiftRepository.findByWeek(week);
    }

    @Transactional
    public ShiftRequirement setShiftRequirement(Long managerId, Long shiftId,
                                                 PositionCode positionCode, int requiredCount) {
        User manager = requireManager(managerId);
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found: " + shiftId));
        Position position = positionRepository.findByPositionCode(positionCode)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found: " + positionCode));

        ShiftRequirement requirement = shiftRequirementRepository
                .findByShiftAndPosition(shift, position)
                .orElseGet(() -> {
                    ShiftRequirement r = new ShiftRequirement();
                    r.setShift(shift);
                    r.setPosition(position);
                    return r;
                });

        requirement.setRequiredCount(requiredCount);
        return shiftRequirementRepository.save(requirement);
    }

    public List<ShiftRequirement> getRequirementsForShift(Long shiftId) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found: " + shiftId));
        return shiftRequirementRepository.findByShift(shift);
    }

    @Transactional
    public Shift assignShiftManager(Long managerId, Long shiftId, Long userId) {
        User manager = requireManager(managerId);
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found: " + shiftId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        if (user.getSystemRole() != SystemRole.SHIFT_MANAGER) {
            throw new BusinessRuleException("User must have SHIFT_MANAGER role to be assigned as shift manager");
        }

        shift.setShiftManager(user);
        Shift saved = shiftRepository.save(shift);
        auditService.log(managerId, "SHIFT_MANAGER_ASSIGN",
                "Assigned " + user.getFullName() + " as shift manager for shift " + shiftId);
        return saved;
    }

    @Transactional
    public ScheduleEntry assignEntry(Long managerId, Long shiftId, Long userId, PositionCode positionCode) {
        User manager = requireManager(managerId);
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found: " + shiftId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        Position position = positionRepository.findByPositionCode(positionCode)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found: " + positionCode));

        if (!user.getQualifications().contains(position)) {
            throw new BusinessRuleException("User is not qualified for position: " + positionCode);
        }

        if (scheduleEntryRepository.findByShiftAndUser(shift, user).isPresent()) {
            throw new BusinessRuleException("User already assigned to this shift");
        }

        ScheduleEntry entry = new ScheduleEntry();
        entry.setShift(shift);
        entry.setUser(user);
        entry.setPosition(position);
        entry.setAssignmentSource(AssignmentSource.MANUAL);
        entry.setAssignedBy(manager);

        ScheduleEntry saved = scheduleEntryRepository.save(entry);
        auditService.log(managerId, "ENTRY_ASSIGN",
                "Assigned " + user.getFullName() + " as " + positionCode + " for shift " + shiftId);
        return saved;
    }

    @Transactional
    public void removeEntry(Long managerId, Long entryId) {
        User manager = requireManager(managerId);
        ScheduleEntry entry = scheduleEntryRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException("ScheduleEntry not found: " + entryId));

        scheduleEntryRepository.delete(entry);
        auditService.log(managerId, "ENTRY_REMOVE",
                "Removed schedule entry " + entryId);
    }

    public List<ScheduleEntry> getEntriesForShift(Long shiftId) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found: " + shiftId));
        return scheduleEntryRepository.findByShift(shift);
    }

    @Transactional
    public ScheduleWeek publishWeek(Long managerId, Long weekId) {
        User manager = requireManager(managerId);
        ScheduleWeek week = scheduleWeekRepository.findById(weekId)
                .orElseThrow(() -> new ResourceNotFoundException("ScheduleWeek not found: " + weekId));

        if (week.getStatus() == WeekStatus.PUBLISHED) {
            throw new BusinessRuleException("Week is already published");
        }

        week.setStatus(WeekStatus.PUBLISHED);
        week.setPublishedAt(LocalDateTime.now());

        ScheduleWeek saved = scheduleWeekRepository.save(week);
        auditService.log(managerId, "WEEK_PUBLISH",
                "Published schedule week starting " + week.getWeekStartDate());
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
}
