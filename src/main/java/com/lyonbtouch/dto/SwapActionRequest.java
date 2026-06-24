package com.lyonbtouch.dto;

import jakarta.validation.constraints.NotNull;

public class SwapActionRequest {

    @NotNull
    private Long approverId;

    public Long getApproverId() {
        return approverId;
    }

    public void setApproverId(Long approverId) {
        this.approverId = approverId;
    }
}
