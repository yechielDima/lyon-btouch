package com.lyonbtouch.controller;

import com.lyonbtouch.dto.*;
import com.lyonbtouch.model.*;
import com.lyonbtouch.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping("/schedule-weeks")
    public ResponseEntity<ScheduleWeekResponse> createWeek(@Valid @RequestBody CreateWeekRequest request) {
        ScheduleWeek week = scheduleService.createWeek(request.getWeekStartDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(DtoMapper.toWeekResponse(week));
    }

    @GetMapping("/schedule-weeks")
    public ResponseEntity<List<ScheduleWeekResponse>> getAllWeeks() {
        List<ScheduleWeekResponse> weeks = scheduleService.getAllWeeks().stream()
                .map(DtoMapper::toWeekResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(weeks);
    }

    @GetMapping("/schedule-weeks/{id}")
    public ResponseEntity<ScheduleWeekResponse> getWeek(@PathVariable Long id) {
        ScheduleWeek week = scheduleService.getWeek(id);
        return ResponseEntity.ok(DtoMapper.toWeekResponse(week));
    }

    @PutMapping("/schedule-weeks/{id}/publish")
    public ResponseEntity<ScheduleWeekResponse> publishWeek(@PathVariable Long id) {
        ScheduleWeek week = scheduleService.publishWeek(id);
        return ResponseEntity.ok(DtoMapper.toWeekResponse(week));
    }

    @PutMapping("/schedule-weeks/{id}/open")
    public ResponseEntity<ScheduleWeekResponse> openWeek(@PathVariable Long id) {
        ScheduleWeek week = scheduleService.openWeek(id);
        return ResponseEntity.ok(DtoMapper.toWeekResponse(week));
    }

    @PostMapping("/schedule-weeks/scaffold")
    public ResponseEntity<ScheduleWeekResponse> scaffoldWeek(@Valid @RequestBody ScaffoldWeekRequest request) {
        ScheduleWeek week = scheduleService.scaffoldWeek(request.getWeekStartDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(DtoMapper.toWeekResponse(week));
    }

    @DeleteMapping("/shifts/{id}")
    public ResponseEntity<Void> deleteShift(@PathVariable Long id) {
        scheduleService.deleteShift(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/shifts")
    public ResponseEntity<ShiftResponse> addShift(@Valid @RequestBody AddShiftRequest request) {
        Shift shift = scheduleService.addShift(
                request.getWeekId(),
                request.getShiftDate(), request.getShiftType());
        return ResponseEntity.status(HttpStatus.CREATED).body(DtoMapper.toShiftResponse(shift));
    }

    @GetMapping("/shifts/{id}")
    public ResponseEntity<ShiftResponse> getShift(@PathVariable Long id) {
        Shift shift = scheduleService.getShift(id);
        return ResponseEntity.ok(DtoMapper.toShiftResponse(shift));
    }

    @GetMapping("/schedule-weeks/{weekId}/shifts")
    public ResponseEntity<List<ShiftResponse>> getShiftsForWeek(@PathVariable Long weekId) {
        List<ShiftResponse> shifts = scheduleService.getShiftsForWeek(weekId).stream()
                .map(DtoMapper::toShiftResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(shifts);
    }

    @PutMapping("/shifts/{id}/shift-manager")
    public ResponseEntity<ShiftResponse> assignShiftManager(@PathVariable Long id,
                                                             @Valid @RequestBody AssignShiftManagerRequest request) {
        Shift shift = scheduleService.assignShiftManager(id, request.getUserId());
        return ResponseEntity.ok(DtoMapper.toShiftResponse(shift));
    }

    @PostMapping("/shifts/{id}/requirements")
    public ResponseEntity<ShiftRequirementResponse> setShiftRequirement(@PathVariable Long id,
                                                                         @Valid @RequestBody ShiftRequirementRequest request) {
        ShiftRequirement req = scheduleService.setShiftRequirement(
                id, request.getPositionCode(), request.getRequiredCount());
        return ResponseEntity.ok(DtoMapper.toRequirementResponse(req));
    }

    @GetMapping("/shifts/{id}/requirements")
    public ResponseEntity<List<ShiftRequirementResponse>> getRequirements(@PathVariable Long id) {
        List<ShiftRequirementResponse> reqs = scheduleService.getRequirementsForShift(id).stream()
                .map(DtoMapper::toRequirementResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reqs);
    }

    @PostMapping("/schedule-entries")
    public ResponseEntity<ScheduleEntryResponse> assignEntry(@Valid @RequestBody AssignEntryRequest request) {
        ScheduleEntry entry = scheduleService.assignEntry(
                request.getShiftId(),
                request.getUserId(), request.getPositionCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(DtoMapper.toEntryResponse(entry));
    }

    @DeleteMapping("/schedule-entries/{id}")
    public ResponseEntity<Void> removeEntry(@PathVariable Long id) {
        scheduleService.removeEntry(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/shifts/{id}/entries")
    public ResponseEntity<List<ScheduleEntryResponse>> getEntriesForShift(@PathVariable Long id) {
        List<ScheduleEntryResponse> entries = scheduleService.getEntriesForShift(id).stream()
                .map(DtoMapper::toEntryResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(entries);
    }
}
