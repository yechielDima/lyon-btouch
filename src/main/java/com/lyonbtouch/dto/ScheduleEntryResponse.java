package com.lyonbtouch.dto;

import com.lyonbtouch.model.enums.AssignmentSource;
import com.lyonbtouch.model.enums.PositionCode;

import java.time.LocalDateTime;

public class ScheduleEntryResponse {

    private Long id;
    private Long shiftId;
    private Long userId;
    private String userName;
    private PositionCode positionCode;
    private AssignmentSource assignmentSource;
    private Long assignedById;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public PositionCode getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(PositionCode positionCode) {
        this.positionCode = positionCode;
    }

    public AssignmentSource getAssignmentSource() {
        return assignmentSource;
    }

    public void setAssignmentSource(AssignmentSource assignmentSource) {
        this.assignmentSource = assignmentSource;
    }

    public Long getAssignedById() {
        return assignedById;
    }

    public void setAssignedById(Long assignedById) {
        this.assignedById = assignedById;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
