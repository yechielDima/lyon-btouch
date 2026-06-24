package com.lyonbtouch.dto;

import jakarta.validation.constraints.NotNull;

public class AvailabilityRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long shiftId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getShiftId() {
        return shiftId;
    }

    public void setShiftId(Long shiftId) {
        this.shiftId = shiftId;
    }
}
