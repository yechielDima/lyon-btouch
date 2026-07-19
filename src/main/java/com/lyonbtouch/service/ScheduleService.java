package com.lyonbtouch.service;

import com.lyonbtouch.exception.BusinessRuleException;
import com.lyonbtouch.exception.ResourceNotFoundException;
import com.lyonbtouch.exception.UnauthorizedException;
import com.lyonbtouch.dto.*;
import com.lyonbtouch.model.*;
import com.lyonbtouch.model.enums.*;
import com.lyonbtouch.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private final ScheduleWeekRepository scheduleWeekRepository;
    private final ShiftRepository shiftRepository;
    private final ShiftRequirementRepository shiftRequirementRepository;
    private final ScheduleEntryRepository scheduleEntryRepository;
    private final UserRepository userRepository;
    private final PositionRepository positionRepository;
    private final AvailabilityRepository availabilityRepository;
    private final SwapRequestRepository swapRequestRepository;
    private final AuditService auditService;

    public ScheduleService(ScheduleWeekRepository scheduleWeekRepository,
                           ShiftRepository shiftRepository,
                           ShiftRequirementRepository shiftRequirementRepository,
                           ScheduleEntryRepository scheduleEntryRepository,
                           UserRepository userRepository,
                           PositionRepository positionRepository,
                           AvailabilityRepository availabilityRepository,
                           SwapRequestRepository swapRequestRepository,
                           AuditService auditService) {
        this.scheduleWeekRepository = scheduleWeekRepository;
        this.shiftRepository = shiftRepository;
        this.shiftRequirementRepository = shiftRequirementRepository;
        this.scheduleEntryRepository = scheduleEntryRepository;
        this.userRepository = userRepository;
        this.positionRepository = positionRepository;
        this.availabilityRepository = availabilityRepository;
        this.swapRequestRepository = swapRequestRepository;
        this.auditService = auditService;
    }

    @Transactional
    public ScheduleWeekResponse createWeek(LocalDate weekStartDate) {
        Long managerId = com.lyonbtouch.security.SecurityUtils.getCurrentUserId();
        User manager = requireManager(managerId);

        if (scheduleWeekRepository.findByWeekStartDate(weekStartDate).isPresent()) {
            throw new BusinessRuleException("Schedule week already exists for date: " + weekStartDate);
        }

        ScheduleWeek week = new ScheduleWeek();
        week.setWeekStartDate(weekStartDate);
        week.setStatus(WeekStatus.DRAFT);

        ScheduleWeek saved = scheduleWeekRepository.save(week);
        auditService.log(managerId, "WEEK_CREATE", "Created schedule week starting " + weekStartDate);
        return DtoMapper.toWeekResponse(saved);
    }

    public ScheduleWeekResponse getWeek(Long weekId) {
        ScheduleWeek week = scheduleWeekRepository.findById(weekId)
                .orElseThrow(() -> new ResourceNotFoundException("ScheduleWeek not found: " + weekId));
        return DtoMapper.toWeekResponse(week);
    }

    public List<ScheduleWeekResponse> getAllWeeks() {
        return scheduleWeekRepository.findAll().stream()
                .map(DtoMapper::toWeekResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ShiftResponse addShift(Long weekId, LocalDate shiftDate, ShiftType shiftType) {
        Long managerId = com.lyonbtouch.security.SecurityUtils.getCurrentUserId();
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
        return DtoMapper.toShiftResponse(saved);
    }

    public ShiftResponse getShift(Long shiftId) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found: " + shiftId));
        return DtoMapper.toShiftResponse(shift);
    }

    @Transactional(readOnly = true)
    public List<ShiftResponse> getShiftsForWeek(Long weekId) {
        ScheduleWeek week = scheduleWeekRepository.findById(weekId)
                .orElseThrow(() -> new ResourceNotFoundException("ScheduleWeek not found: " + weekId));
        return shiftRepository.findByWeek(week).stream()
                .map(DtoMapper::toShiftResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ShiftRequirementResponse setShiftRequirement(Long shiftId,
                                                 PositionCode positionCode, int requiredCount) {
        Long managerId = com.lyonbtouch.security.SecurityUtils.getCurrentUserId();
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
        ShiftRequirement saved = shiftRequirementRepository.save(requirement);
        return DtoMapper.toRequirementResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ShiftRequirementResponse> getRequirementsForShift(Long shiftId) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found: " + shiftId));
        return shiftRequirementRepository.findByShift(shift).stream()
                .map(DtoMapper::toRequirementResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ShiftResponse assignShiftManager(Long shiftId, Long userId) {
        Long managerId = com.lyonbtouch.security.SecurityUtils.getCurrentUserId();
        User manager = requireManager(managerId);
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found: " + shiftId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        if (user.getSystemRole() != SystemRole.SHIFT_MANAGER) {
            throw new BusinessRuleException("User must have SHIFT_MANAGER role to be assigned as shift manager");
        }

        if (scheduleEntryRepository.findByShiftAndUser(shift, user).isPresent()) {
            throw new BusinessRuleException("Cannot assign as shift manager: user already has a schedule entry in this shift");
        }

        shift.setShiftManager(user);
        Shift saved = shiftRepository.save(shift);
        auditService.log(managerId, "SHIFT_MANAGER_ASSIGN",
                "Assigned " + user.getFullName() + " as shift manager for shift " + shiftId);
        return DtoMapper.toShiftResponse(saved);
    }

    @Transactional
    public ScheduleEntryResponse assignEntry(Long shiftId, Long userId, PositionCode positionCode) {
        Long managerId = com.lyonbtouch.security.SecurityUtils.getCurrentUserId();
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

        if (shift.getShiftManager() != null && shift.getShiftManager().getId().equals(user.getId())) {
            throw new BusinessRuleException("Cannot assign schedule entry: user is the shift manager for this shift");
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
        return DtoMapper.toEntryResponse(saved);
    }

    @Transactional
    public void removeEntry(Long entryId) {
        Long managerId = com.lyonbtouch.security.SecurityUtils.getCurrentUserId();
        User manager = requireManager(managerId);
        ScheduleEntry entry = scheduleEntryRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException("ScheduleEntry not found: " + entryId));

        scheduleEntryRepository.delete(entry);
        auditService.log(managerId, "ENTRY_REMOVE",
                "Removed schedule entry " + entryId);
    }

    @Transactional(readOnly = true)
    public List<ScheduleEntryResponse> getEntriesForShift(Long shiftId) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found: " + shiftId));
        return scheduleEntryRepository.findByShift(shift).stream()
                .map(DtoMapper::toEntryResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ScheduleWeekResponse publishWeek(Long weekId) {
        Long managerId = com.lyonbtouch.security.SecurityUtils.getCurrentUserId();
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
        return DtoMapper.toWeekResponse(saved);
    }

    @Transactional
    public ScheduleWeekResponse openWeek(Long weekId) {
        Long managerId = com.lyonbtouch.security.SecurityUtils.getCurrentUserId();
        User manager = requireManager(managerId);
        ScheduleWeek week = scheduleWeekRepository.findById(weekId)
                .orElseThrow(() -> new ResourceNotFoundException("ScheduleWeek not found: " + weekId));

        if (week.getStatus() == WeekStatus.PUBLISHED) {
            throw new BusinessRuleException("Cannot open a published week");
        }
        if (week.getStatus() == WeekStatus.OPEN_FOR_SUBMISSION) {
            throw new BusinessRuleException("Week is already open for submission");
        }

        week.setStatus(WeekStatus.OPEN_FOR_SUBMISSION);

        ScheduleWeek saved = scheduleWeekRepository.save(week);
        auditService.log(managerId, "WEEK_OPEN",
                "Opened schedule week starting " + week.getWeekStartDate() + " for submission");
        return DtoMapper.toWeekResponse(saved);
    }

    @Transactional
    public ScheduleWeekResponse scaffoldWeek(LocalDate weekStartDate) {
        Long managerId = com.lyonbtouch.security.SecurityUtils.getCurrentUserId();
        User manager = requireManager(managerId);

        if (weekStartDate.getDayOfWeek() != java.time.DayOfWeek.SUNDAY) {
            throw new BusinessRuleException("Week start date must be a Sunday");
        }

        if (scheduleWeekRepository.findByWeekStartDate(weekStartDate).isPresent()) {
            throw new BusinessRuleException("Schedule week already exists for date: " + weekStartDate);
        }

        ScheduleWeek week = new ScheduleWeek();
        week.setWeekStartDate(weekStartDate);
        week.setStatus(WeekStatus.DRAFT);
        ScheduleWeek savedWeek = scheduleWeekRepository.save(week);

        for (int i = 0; i < 5; i++) {
            LocalDate date = weekStartDate.plusDays(i);
            Shift morning = new Shift();
            morning.setWeek(savedWeek);
            morning.setShiftDate(date);
            morning.setShiftType(ShiftType.MORNING);
            shiftRepository.save(morning);

            Shift evening = new Shift();
            evening.setWeek(savedWeek);
            evening.setShiftDate(date);
            evening.setShiftType(ShiftType.EVENING);
            shiftRepository.save(evening);
        }

        Shift friday = new Shift();
        friday.setWeek(savedWeek);
        friday.setShiftDate(weekStartDate.plusDays(5));
        friday.setShiftType(ShiftType.FRIDAY);
        shiftRepository.save(friday);

        Shift motzash = new Shift();
        motzash.setWeek(savedWeek);
        motzash.setShiftDate(weekStartDate.plusDays(6));
        motzash.setShiftType(ShiftType.MOTZASH);
        shiftRepository.save(motzash);

        auditService.log(managerId, "WEEK_SCAFFOLD", "Scaffolded schedule week starting " + weekStartDate);
        return DtoMapper.toWeekResponse(savedWeek);
    }

    @Transactional
    public void deleteShift(Long shiftId) {
        Long managerId = com.lyonbtouch.security.SecurityUtils.getCurrentUserId();
        User manager = requireManager(managerId);

        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found: " + shiftId));

        if (shift.getWeek().getStatus() == WeekStatus.PUBLISHED) {
            throw new BusinessRuleException("Cannot delete shift from a published week");
        }

        List<ScheduleEntry> entries = scheduleEntryRepository.findByShift(shift);
        for (ScheduleEntry entry : entries) {
            List<SwapRequest> swaps = swapRequestRepository.findByScheduleEntry(entry);
            swapRequestRepository.deleteAll(swaps);
        }

        scheduleEntryRepository.deleteAll(entries);

        List<Availability> availabilities = availabilityRepository.findByShift(shift);
        availabilityRepository.deleteAll(availabilities);

        List<ShiftRequirement> requirements = shiftRequirementRepository.findByShift(shift);
        shiftRequirementRepository.deleteAll(requirements);

        shiftRepository.delete(shift);

        auditService.log(managerId, "SHIFT_DELETE", "Deleted shift " + shiftId + " (" + shift.getShiftType() + " on " + shift.getShiftDate() + ")");
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
