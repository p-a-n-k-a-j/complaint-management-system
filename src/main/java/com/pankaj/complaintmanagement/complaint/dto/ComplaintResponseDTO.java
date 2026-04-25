package com.pankaj.complaintmanagement.complaint.dto;

import com.pankaj.complaintmanagement.common.enums.ComplaintStatus;
import com.pankaj.complaintmanagement.common.enums.Priority;
import com.pankaj.complaintmanagement.entity.ComplaintAttachment;
import com.pankaj.complaintmanagement.util.ComplaintCategory;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;

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
    private Priority priority;
    private String nameInitials; //ager image nhi mila to me placeholder me uska name ka first latter bhejunga.
    private String name;   // Extra call se bachne ke liye (optional)
    private String email;
    private String imageUrl;
    private String assignedTo;
    private LocalDateTime resolvedAt;
    private String resolutionTime;
    private List<ComplaintAttachment> attachments;

    public String getResolutionTime() {
        return resolutionTime;
    }

    public void setResolutionTime(String resolutionTime) {
        this.resolutionTime = resolutionTime;
    }

    public String getNameInitials() {
        return nameInitials;
    }

    public void setNameInitials(String nameInitials) {
        this.nameInitials = nameInitials;
    }

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

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public List<ComplaintAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<ComplaintAttachment> attachments) {
        this.attachments = attachments;
    }

    //    public String getImageUrl(String imageName){
//        return ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/images/")
//                .path(imageName)
//                .toUriString();
//    }
}
