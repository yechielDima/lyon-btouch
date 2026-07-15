package com.lyonbtouch.controller;

import com.lyonbtouch.dto.*;
import com.lyonbtouch.model.Availability;
import com.lyonbtouch.service.AvailabilityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @PostMapping("/availability")
    public ResponseEntity<AvailabilityResponse> submitAvailability(@Valid @RequestBody AvailabilityRequest request) {
        Availability availability = availabilityService.submitAvailability(request.getShiftId());
        return ResponseEntity.status(HttpStatus.CREATED).body(DtoMapper.toAvailabilityResponse(availability));
    }

    @DeleteMapping("/availability")
    public ResponseEntity<Void> removeAvailability(@RequestParam Long shiftId) {
        availabilityService.removeAvailability(shiftId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/shifts/{shiftId}/availability")
    public ResponseEntity<List<AvailabilityResponse>> getAvailabilityForShift(@PathVariable Long shiftId) {
        List<AvailabilityResponse> list = availabilityService.getAvailabilityForShift(shiftId).stream()
                .map(DtoMapper::toAvailabilityResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/users/{userId}/availability")
    public ResponseEntity<List<AvailabilityResponse>> getAvailabilityForUser(@PathVariable Long userId) {
        List<AvailabilityResponse> list = availabilityService.getAvailabilityForUser(userId).stream()
                .map(DtoMapper::toAvailabilityResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
}
