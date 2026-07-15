package com.lyonbtouch.dto;

import jakarta.validation.constraints.NotNull;

public class AssignShiftManagerRequest {
    @NotNull
    private Long userId;
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
