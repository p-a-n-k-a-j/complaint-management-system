package com.pankaj.complaintmanagement.complaint.dto;

import com.pankaj.complaintmanagement.util.ComplaintCategory;

public class ComplaintRequest {
    private Long complaintId;
    private String title;
    private String description;
    private String remark;
    private ComplaintCategory category;


    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long complaintId) {
        this.complaintId = complaintId;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public ComplaintCategory getCategory() {
        return category;
    }

    public void setCategory(ComplaintCategory category) {
        this.category = category;
    }
}
