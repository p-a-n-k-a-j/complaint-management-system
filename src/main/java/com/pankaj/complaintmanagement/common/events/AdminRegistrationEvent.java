package com.pankaj.complaintmanagement.common.events;

public record AdminRegistrationEvent(String recipientEmail, String name, String temPassword) {
}
