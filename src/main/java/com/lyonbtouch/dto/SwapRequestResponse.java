package com.lyonbtouch.dto;

import com.lyonbtouch.model.enums.PositionCode;
import com.lyonbtouch.model.enums.SwapStatus;

import java.time.LocalDateTime;

public class SwapRequestResponse {

    private Long id;
    private Long scheduleEntryId;
    private Long requestingUserId;
    private String requestingUserName;
    private Long coveringUserId;
    private String coveringUserName;
    private SwapStatus status;
    private Long approvedById;
    private PositionCode positionCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getScheduleEntryId() {
        return scheduleEntryId;
    }

    public void setScheduleEntryId(Long scheduleEntryId) {
        this.scheduleEntryId = scheduleEntryId;
    }

    public Long getRequestingUserId() {
        return requestingUserId;
    }

    public void setRequestingUserId(Long requestingUserId) {
        this.requestingUserId = requestingUserId;
    }

    public String getRequestingUserName() {
        return requestingUserName;
    }

    public void setRequestingUserName(String requestingUserName) {
        this.requestingUserName = requestingUserName;
    }

    public Long getCoveringUserId() {
        return coveringUserId;
    }

    public void setCoveringUserId(Long coveringUserId) {
        this.coveringUserId = coveringUserId;
    }

    public String getCoveringUserName() {
        return coveringUserName;
    }

    public void setCoveringUserName(String coveringUserName) {
        this.coveringUserName = coveringUserName;
    }

    public SwapStatus getStatus() {
        return status;
    }

    public void setStatus(SwapStatus status) {
        this.status = status;
    }

    public Long getApprovedById() {
        return approvedById;
    }

    public void setApprovedById(Long approvedById) {
        this.approvedById = approvedById;
    }

    public PositionCode getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(PositionCode positionCode) {
        this.positionCode = positionCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
