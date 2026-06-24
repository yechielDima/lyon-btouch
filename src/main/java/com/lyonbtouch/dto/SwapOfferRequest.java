package com.lyonbtouch.dto;

import jakarta.validation.constraints.NotNull;

public class SwapOfferRequest {

    @NotNull
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
