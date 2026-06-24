package com.lyonbtouch.dto;

import jakarta.validation.constraints.NotNull;

public class SwapOpenRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long entryId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEntryId() {
        return entryId;
    }

    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }
}
