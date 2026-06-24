package com.lyonbtouch.dto;

import com.lyonbtouch.model.enums.ShiftType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ShiftResponse {

    private Long id;
    private Long weekId;
    private LocalDate shiftDate;
    private ShiftType shiftType;
    private Long shiftManagerId;
    private String shiftManagerName;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getShiftManagerId() {
        return shiftManagerId;
    }

    public void setShiftManagerId(Long shiftManagerId) {
        this.shiftManagerId = shiftManagerId;
    }

    public String getShiftManagerName() {
        return shiftManagerName;
    }

    public void setShiftManagerName(String shiftManagerName) {
        this.shiftManagerName = shiftManagerName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
