package com.lyonbtouch.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ScaffoldWeekRequest {

    @NotNull(message = "weekStartDate is required")
    private LocalDate weekStartDate;

    public LocalDate getWeekStartDate() {
        return weekStartDate;
    }

    public void setWeekStartDate(LocalDate weekStartDate) {
        this.weekStartDate = weekStartDate;
    }
}
