package com.pankaj.complaintmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pankaj.complaintmanagement.common.enums.ComplaintStatus;
import com.pankaj.complaintmanagement.common.enums.Priority;
import com.pankaj.complaintmanagement.util.ComplaintCategory;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String ticketId; // Unique Ticket Number

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private ComplaintStatus status;
    // Kisine toh complaint ki hogi!
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // One Complaint -> Many Logs
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "complaint")
    @JsonIgnoreProperties
    private List<ComplaintLog> complaintLogs = new ArrayList<>();
    @Column(columnDefinition = "TEXT")
    private String remark; // Admin ka final comment
    @Column(name = "action_by_id")
    private User updatedBy; // Admin ka naam/email jisne last change kiya
    @Enumerated(EnumType.STRING)
    private ComplaintCategory category;

    @Enumerated(EnumType.STRING)
    private Priority priority; // optional
    private LocalDateTime resolvedAt; // Analytics ke liye (optional)
    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;
    @OneToMany(mappedBy = "complaint", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties
    private List<ComplaintAttachment> complaintAttachment = new ArrayList<>();


    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }

    public List<ComplaintAttachment> getComplaintAttachment() {
        return complaintAttachment;
    }

    public void setComplaintAttachment(List<ComplaintAttachment> complaintAttachment) {
        this.complaintAttachment = complaintAttachment;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Priority getPriority() {
        return priority;
    }
    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public ComplaintCategory getCategory() {
        return category;
    }

    public void setCategory(ComplaintCategory category) {
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public List<ComplaintLog> getComplaintLogs() {
        return complaintLogs;
    }

    public void setComplaintLogs(List<ComplaintLog> complaintLogs) {
        this.complaintLogs = complaintLogs;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }
}

