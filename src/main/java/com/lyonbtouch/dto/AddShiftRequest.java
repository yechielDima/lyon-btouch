package com.lyonbtouch.dto;

import com.lyonbtouch.model.enums.ShiftType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class AddShiftRequest {

    @NotNull
    private Long managerId;

    @NotNull
    private Long weekId;

    @NotNull
    private LocalDate shiftDate;

    @NotNull
    private ShiftType shiftType;

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public Long getWeekId() {
        return weekId;
    }

    public void setWeekId(Long weekId) {
        this.weekId = weekId;
    }

    public LocalDate getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(LocalDate shiftDate) {
        this.shiftDate = shiftDate;
    }

    public ShiftType getShiftType() {
        return shiftType;
    }

    public void setShiftType(ShiftType shiftType) {
        this.shiftType = shiftType;
    }
}
