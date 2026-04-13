package com.pankaj.complaintmanagement.complaint.dto;

import com.pankaj.complaintmanagement.common.enums.Priority;
import com.pankaj.complaintmanagement.util.ComplaintCategory;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ComplaintRequest {
    private String title;
    private String description;
    private String remark;
    private ComplaintCategory category;
    private Priority priority;
    private Long assignedToId;
    private List<MultipartFile> attachments;

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Long getAssignedToId() {
        return assignedToId;
    }

    public void setAssignedToId(Long assignedToId) {
        this.assignedToId = assignedToId;
    }

    public List<MultipartFile> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<MultipartFile> attachments) {
        this.attachments = attachments;
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
