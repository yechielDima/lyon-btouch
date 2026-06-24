package com.lyonbtouch.dto;

import com.lyonbtouch.model.enums.PositionCode;
import com.lyonbtouch.model.enums.SystemRole;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class ApproveUserRequest {

    @NotNull
    private Long managerId;

    @NotNull
    private SystemRole systemRole;

    private List<PositionCode> qualifications;

    private boolean isChecker;

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

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

    public boolean isChecker() {
        return isChecker;
    }

    public void setChecker(boolean checker) {
        isChecker = checker;
    }
}
