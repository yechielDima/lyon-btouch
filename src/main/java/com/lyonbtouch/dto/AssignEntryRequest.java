package com.lyonbtouch.dto;

import com.lyonbtouch.model.enums.PositionCode;
import jakarta.validation.constraints.NotNull;

public class AssignEntryRequest {

    @NotNull
    private Long managerId;

    @NotNull
    private Long shiftId;

    @NotNull
    private Long userId;

    @NotNull
    private PositionCode positionCode;

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

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
