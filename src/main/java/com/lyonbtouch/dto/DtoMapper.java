package com.lyonbtouch.dto;

import com.lyonbtouch.model.*;

import java.util.stream.Collectors;

public final class DtoMapper {

    private DtoMapper() {
    }

    public static UserResponse toUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setSystemRole(user.getSystemRole());
        response.setAccountStatus(user.getAccountStatus());
        response.setChecker(user.isChecker());
        response.setReliabilityScore(user.getReliabilityScore());
        response.setActive(user.isActive());
        response.setCreatedAt(user.getCreatedAt());
        response.setQualifications(
                user.getQualifications().stream()
                        .map(Position::getPositionCode)
                        .collect(Collectors.toList())
        );
        return response;
    }

    public static ScheduleWeekResponse toWeekResponse(ScheduleWeek week) {
        ScheduleWeekResponse response = new ScheduleWeekResponse();
        response.setId(week.getId());
        response.setWeekStartDate(week.getWeekStartDate());
        response.setStatus(week.getStatus());
        response.setGeneratedAt(week.getGeneratedAt());
        response.setPublishedAt(week.getPublishedAt());
        response.setCreatedAt(week.getCreatedAt());
        if (week.getGeneratedBy() != null) {
            response.setGeneratedById(week.getGeneratedBy().getId());
        }
        return response;
    }

    public static ShiftResponse toShiftResponse(Shift shift) {
        ShiftResponse response = new ShiftResponse();
        response.setId(shift.getId());
        response.setWeekId(shift.getWeek().getId());
        response.setShiftDate(shift.getShiftDate());
        response.setShiftType(shift.getShiftType());
        response.setCreatedAt(shift.getCreatedAt());
        if (shift.getShiftManager() != null) {
            response.setShiftManagerId(shift.getShiftManager().getId());
            response.setShiftManagerName(shift.getShiftManager().getFullName());
        }
        return response;
    }

    public static ShiftRequirementResponse toRequirementResponse(ShiftRequirement req) {
        ShiftRequirementResponse response = new ShiftRequirementResponse();
        response.setId(req.getId());
        response.setShiftId(req.getShift().getId());
        response.setPositionCode(req.getPosition().getPositionCode());
        response.setRequiredCount(req.getRequiredCount());
        return response;
    }

    public static AvailabilityResponse toAvailabilityResponse(Availability availability) {
        AvailabilityResponse response = new AvailabilityResponse();
        response.setId(availability.getId());
        response.setUserId(availability.getUser().getId());
        response.setUserName(availability.getUser().getFullName());
        response.setShiftId(availability.getShift().getId());
        response.setCreatedAt(availability.getCreatedAt());
        return response;
    }

    public static ScheduleEntryResponse toEntryResponse(ScheduleEntry entry) {
        ScheduleEntryResponse response = new ScheduleEntryResponse();
        response.setId(entry.getId());
        response.setShiftId(entry.getShift().getId());
        response.setUserId(entry.getUser().getId());
        response.setUserName(entry.getUser().getFullName());
        response.setPositionCode(entry.getPosition().getPositionCode());
        response.setAssignmentSource(entry.getAssignmentSource());
        response.setCreatedAt(entry.getCreatedAt());
        if (entry.getAssignedBy() != null) {
            response.setAssignedById(entry.getAssignedBy().getId());
        }
        return response;
    }

    public static SwapRequestResponse toSwapResponse(SwapRequest swap) {
        SwapRequestResponse response = new SwapRequestResponse();
        response.setId(swap.getId());
        response.setScheduleEntryId(swap.getScheduleEntry().getId());
        response.setRequestingUserId(swap.getRequestingUser().getId());
        response.setRequestingUserName(swap.getRequestingUser().getFullName());
        response.setStatus(swap.getStatus());
        response.setCreatedAt(swap.getCreatedAt());
        response.setUpdatedAt(swap.getUpdatedAt());
        response.setPositionCode(swap.getScheduleEntry().getPosition().getPositionCode());
        if (swap.getCoveringUser() != null) {
            response.setCoveringUserId(swap.getCoveringUser().getId());
            response.setCoveringUserName(swap.getCoveringUser().getFullName());
        }
        if (swap.getApprovedBy() != null) {
            response.setApprovedById(swap.getApprovedBy().getId());
        }
        return response;
    }
}
