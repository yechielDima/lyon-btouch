package com.lyonbtouch.dto;

import jakarta.validation.constraints.NotBlank;

public class RequestCodeRequest {

    @NotBlank
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
