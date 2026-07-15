package com.lyonbtouch.dto;

import jakarta.validation.constraints.NotNull;

public class SwapOpenRequest {
    @NotNull
    private Long entryId;
    public Long getEntryId() {
        return entryId;
    }

    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }
}
