package com.pankaj.complaintmanagement.common.events;

import com.pankaj.complaintmanagement.entity.Complaint;

public record ComplaintAssignedEvent(
        String adminEmail,
        String adminName,
        String ticketId,
        String userEmail,
        String priority
) {}
