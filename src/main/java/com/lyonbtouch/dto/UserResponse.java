package com.lyonbtouch.dto;

import com.lyonbtouch.model.enums.AccountStatus;
import com.lyonbtouch.model.enums.PositionCode;
import com.lyonbtouch.model.enums.SystemRole;

import java.time.LocalDateTime;
import java.util.List;

public class UserResponse {

    private Long id;
    private String fullName;
    private String phone;
    private SystemRole systemRole;
    private AccountStatus accountStatus;
    private boolean isChecker;
    private int reliabilityScore;
    private boolean active;
    private List<PositionCode> qualifications;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public SystemRole getSystemRole() {
        return systemRole;
    }

    public void setSystemRole(SystemRole systemRole) {
        this.systemRole = systemRole;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public boolean isChecker() {
        return isChecker;
    }

    public void setChecker(boolean checker) {
        isChecker = checker;
    }

    public int getReliabilityScore() {
        return reliabilityScore;
    }

    public void setReliabilityScore(int reliabilityScore) {
        this.reliabilityScore = reliabilityScore;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<PositionCode> getQualifications() {
        return qualifications;
    }

    public void setQualifications(List<PositionCode> qualifications) {
        this.qualifications = qualifications;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
