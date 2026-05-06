package com.pankaj.complaintmanagement.common.events;

public record RemarkUpdateEvent(String recipientEmail, String remark, String ticketId) {
}
