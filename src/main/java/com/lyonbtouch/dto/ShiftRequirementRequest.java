package com.lyonbtouch.dto;

import com.lyonbtouch.model.enums.PositionCode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ShiftRequirementRequest {

    @NotNull
    private Long managerId;

    @NotNull
    private PositionCode positionCode;

    @Min(0)
    private int requiredCount;

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public PositionCode getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(PositionCode positionCode) {
        this.positionCode = positionCode;
    }

    public int getRequiredCount() {
        return requiredCount;
    }

    public void setRequiredCount(int requiredCount) {
        this.requiredCount = requiredCount;
    }
}
