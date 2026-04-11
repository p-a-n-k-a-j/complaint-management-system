package com.pankaj.complaintmanagement.complaint.dto;

import com.pankaj.complaintmanagement.common.enums.ComplaintStatus;
import com.pankaj.complaintmanagement.common.enums.Priority;
import com.pankaj.complaintmanagement.util.ComplaintCategory;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

public class ComplaintResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String remark;
    private String ticketId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ComplaintStatus status;
    private ComplaintCategory category;
    private String updatedBy;
    //user-details userId se fetch kar lega frontEnd
    private Long userId;
    private Priority priority;
    private String name;   // Extra call se bachne ke liye (optional)
    private String email;
    private String imageUrl;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public ComplaintCategory getCategory() {
        return category;
    }

    public void setCategory(ComplaintCategory category) {
        this.category = category;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getImageUrl(String imageName){
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/images/")
                .path(imageName)
                .toUriString();
    }
}
