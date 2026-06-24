package com.lyonbtouch.dto;

import com.lyonbtouch.model.enums.WeekStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ScheduleWeekResponse {

    private Long id;
    private LocalDate weekStartDate;
    private WeekStatus status;
    private Long generatedById;
    private LocalDateTime generatedAt;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getWeekStartDate() {
        return weekStartDate;
    }

    public void setWeekStartDate(LocalDate weekStartDate) {
        this.weekStartDate = weekStartDate;
    }

    public WeekStatus getStatus() {
        return status;
    }

    public void setStatus(WeekStatus status) {
        this.status = status;
    }

    public Long getGeneratedById() {
        return generatedById;
    }

    public void setGeneratedById(Long generatedById) {
        this.generatedById = generatedById;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
