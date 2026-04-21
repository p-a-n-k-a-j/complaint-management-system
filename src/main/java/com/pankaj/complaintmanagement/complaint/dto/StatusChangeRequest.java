package com.pankaj.complaintmanagement.complaint.dto;

import com.pankaj.complaintmanagement.common.enums.ComplaintStatus;
import jakarta.validation.constraints.NotNull;

public class StatusChangeRequest {
    @NotNull(message = "Complaint id is required")
    private Long complaintId;//todo: this is mandatory
    @NotNull(message = "Complaint status can't be empty")
    private ComplaintStatus status;
    private String remark;

    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long complaintId) {
        this.complaintId = complaintId;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
