package com.lyonbtouch.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class CreateWeekRequest {

    @NotNull
    private Long managerId;

    @NotNull
    private LocalDate weekStartDate;

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public LocalDate getWeekStartDate() {
        return weekStartDate;
    }

    public void setWeekStartDate(LocalDate weekStartDate) {
        this.weekStartDate = weekStartDate;
    }
}
