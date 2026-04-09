package com.pankaj.complaintmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pankaj.complaintmanagement.common.enums.ComplaintStatus;
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

    private String complaintTitle;

    @Column(columnDefinition = "TEXT")
    private String complaintDescription;

    @Enumerated(EnumType.STRING)
    private ComplaintStatus complaintStatus;

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
    private String actionBy; // Admin ka naam/email jisne last change kiya
    @Enumerated(EnumType.STRING)
    private ComplaintCategory category;



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

    public String getComplaintTitle() {
        return complaintTitle;
    }

    public void setComplaintTitle(String complaintTitle) {
        this.complaintTitle = complaintTitle;
    }

    public String getComplaintDescription() {
        return complaintDescription;
    }

    public void setComplaintDescription(String complaintDescription) {
        this.complaintDescription = complaintDescription;
    }

    public ComplaintStatus getComplaintStatus() {
        return complaintStatus;
    }

    public void setComplaintStatus(ComplaintStatus complaintStatus) {
        this.complaintStatus = complaintStatus;
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

    public String getActionBy() {
        return actionBy;
    }

    public void setActionBy(String actionBy) {
        this.actionBy = actionBy;
    }
}

