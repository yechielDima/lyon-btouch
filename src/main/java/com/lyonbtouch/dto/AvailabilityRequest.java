package com.lyonbtouch.dto;

import jakarta.validation.constraints.NotNull;

public class AvailabilityRequest {
    @NotNull
    private Long shiftId;
    public Long getShiftId() {
        return shiftId;
    }

    public void setShiftId(Long shiftId) {
        this.shiftId = shiftId;
    }
}
