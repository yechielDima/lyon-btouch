package com.lyonbtouch.dto;

import com.lyonbtouch.model.enums.PositionCode;
import com.lyonbtouch.model.enums.SystemRole;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class UpdateUserRequest {
    private SystemRole systemRole;

    private List<PositionCode> qualifications;

    private Boolean isChecker;

    private Boolean active;
    public SystemRole getSystemRole() {
        return systemRole;
    }

    public void setSystemRole(SystemRole systemRole) {
        this.systemRole = systemRole;
    }

    public List<PositionCode> getQualifications() {
        return qualifications;
    }

    public void setQualifications(List<PositionCode> qualifications) {
        this.qualifications = qualifications;
    }

    public Boolean getIsChecker() {
        return isChecker;
    }

    public void setIsChecker(Boolean isChecker) {
        this.isChecker = isChecker;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
