package com.pankaj.complaintmanagement.complaint.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SetRemarkRequest {
    @NotNull(message = "complaint id is required")
    private Long complaintId;
    @NotBlank(message = "remark is mandatory for this request")
    private String remark;

    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long complaintId) {
        this.complaintId = complaintId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
