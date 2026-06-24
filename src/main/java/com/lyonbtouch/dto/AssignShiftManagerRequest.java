package com.lyonbtouch.dto;

import jakarta.validation.constraints.NotNull;

public class AssignShiftManagerRequest {

    @NotNull
    private Long managerId;

    @NotNull
    private Long userId;

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
