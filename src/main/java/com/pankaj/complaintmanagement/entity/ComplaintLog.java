package com.pankaj.complaintmanagement.entity;

import com.pankaj.complaintmanagement.common.enums.ComplaintStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
@Entity
public class ComplaintLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "complaint_id")
    private Complaint complaint;
    @Column(unique = true, nullable = false)
    private String ticketId;
    @Enumerated(EnumType.STRING)
    private ComplaintStatus previousStatus;
    @Enumerated(EnumType.STRING)
    private ComplaintStatus newStatus;
    @Column(columnDefinition = "TEXT")
    private String remark;
    private LocalDateTime logTime;
    @ManyToOne
    @JoinColumn(name = "action_by_id")
    private User actionBy;

    public ComplaintStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(ComplaintStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public ComplaintStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(ComplaintStatus newStatus) {
        this.newStatus = newStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Complaint getComplaint() {
        return complaint;
    }

    public void setComplaint(Complaint complaint) {
        this.complaint = complaint;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getLogTime() {
        return logTime;
    }

    public void setLogTime(LocalDateTime logTime) {
        this.logTime = logTime;
    }

    public User getActionBy() {
        return actionBy;
    }

    public void setActionBy(User actionBy) {
        this.actionBy = actionBy;
    }
}
