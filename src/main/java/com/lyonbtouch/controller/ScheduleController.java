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
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleService.createWeek(request.getWeekStartDate()));
    }

    @GetMapping("/schedule-weeks")
    public ResponseEntity<List<ScheduleWeekResponse>> getAllWeeks() {
        return ResponseEntity.ok(scheduleService.getAllWeeks());
    }

    @GetMapping("/schedule-weeks/{id}")
    public ResponseEntity<ScheduleWeekResponse> getWeek(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getWeek(id));
    }

    @PutMapping("/schedule-weeks/{id}/publish")
    public ResponseEntity<ScheduleWeekResponse> publishWeek(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.publishWeek(id));
    }

    @PutMapping("/schedule-weeks/{id}/open")
    public ResponseEntity<ScheduleWeekResponse> openWeek(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.openWeek(id));
    }

    @PostMapping("/schedule-weeks/scaffold")
    public ResponseEntity<ScheduleWeekResponse> scaffoldWeek(@Valid @RequestBody ScaffoldWeekRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleService.scaffoldWeek(request.getWeekStartDate()));
    }

    @DeleteMapping("/shifts/{id}")
    public ResponseEntity<Void> deleteShift(@PathVariable Long id) {
        scheduleService.deleteShift(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/shifts")
    public ResponseEntity<ShiftResponse> addShift(@Valid @RequestBody AddShiftRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                scheduleService.addShift(request.getWeekId(), request.getShiftDate(), request.getShiftType())
        );
    }

    @GetMapping("/shifts/{id}")
    public ResponseEntity<ShiftResponse> getShift(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getShift(id));
    }

    @GetMapping("/schedule-weeks/{weekId}/shifts")
    public ResponseEntity<List<ShiftResponse>> getShiftsForWeek(@PathVariable Long weekId) {
        return ResponseEntity.ok(scheduleService.getShiftsForWeek(weekId));
    }

    @PutMapping("/shifts/{id}/shift-manager")
    public ResponseEntity<ShiftResponse> assignShiftManager(@PathVariable Long id,
                                                             @Valid @RequestBody AssignShiftManagerRequest request) {
        return ResponseEntity.ok(scheduleService.assignShiftManager(id, request.getUserId()));
    }

    @PostMapping("/shifts/{id}/requirements")
    public ResponseEntity<ShiftRequirementResponse> setShiftRequirement(@PathVariable Long id,
                                                                         @Valid @RequestBody ShiftRequirementRequest request) {
        return ResponseEntity.ok(scheduleService.setShiftRequirement(id, request.getPositionCode(), request.getRequiredCount()));
    }

    @GetMapping("/shifts/{id}/requirements")
    public ResponseEntity<List<ShiftRequirementResponse>> getRequirements(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getRequirementsForShift(id));
    }

    @PostMapping("/schedule-entries")
    public ResponseEntity<ScheduleEntryResponse> assignEntry(@Valid @RequestBody AssignEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                scheduleService.assignEntry(request.getShiftId(), request.getUserId(), request.getPositionCode())
        );
    }

    @DeleteMapping("/schedule-entries/{id}")
    public ResponseEntity<Void> removeEntry(@PathVariable Long id) {
        scheduleService.removeEntry(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/shifts/{id}/entries")
    public ResponseEntity<List<ScheduleEntryResponse>> getEntriesForShift(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getEntriesForShift(id));
    }
}
