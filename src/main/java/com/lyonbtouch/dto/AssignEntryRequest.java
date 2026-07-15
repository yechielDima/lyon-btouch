package com.lyonbtouch.dto;

import com.lyonbtouch.model.enums.PositionCode;
import jakarta.validation.constraints.NotNull;

public class AssignEntryRequest {
    @NotNull
    private Long shiftId;

    @NotNull
    private Long userId;

    @NotNull
    private PositionCode positionCode;
    public Long getShiftId() {
        return shiftId;
    }

    public void setShiftId(Long shiftId) {
        this.shiftId = shiftId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public PositionCode getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(PositionCode positionCode) {
        this.positionCode = positionCode;
    }
}
