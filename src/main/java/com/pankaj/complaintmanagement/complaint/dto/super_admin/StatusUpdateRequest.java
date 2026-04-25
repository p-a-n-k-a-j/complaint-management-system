package com.pankaj.complaintmanagement.complaint.dto.super_admin;

import com.pankaj.complaintmanagement.common.enums.AccountStatus;

public class StatusUpdateRequest {
    private AccountStatus status;

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}
