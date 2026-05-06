package com.pankaj.complaintmanagement.common.events;

import com.pankaj.complaintmanagement.common.enums.ComplaintStatus;

public record UpdateComplaintStatusEvent(String recipientEmail, String name, String remark, ComplaintStatus status, String ticketId) {
}
