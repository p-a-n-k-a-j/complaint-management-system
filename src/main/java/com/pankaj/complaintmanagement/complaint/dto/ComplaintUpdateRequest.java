package com.pankaj.complaintmanagement.complaint.dto;

import com.pankaj.complaintmanagement.common.enums.Priority;
import com.pankaj.complaintmanagement.util.ComplaintCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ComplaintUpdateRequest {
    @NotNull(message = "ComplaintId can't be null")
    private Long complaintId;
    @NotBlank(message = "Title is required to update")
    private String title;
    @NotBlank(message = "Description is required to update")
    private String description;
    private String remark;
    @NotNull(message = "Priority is mandatory")
    private Priority priority;
    @NotNull(message = "Category is required")
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

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public ComplaintCategory getCategory() {
        return category;
    }

    public void setCategory(ComplaintCategory category) {
        this.category = category;
    }
}
