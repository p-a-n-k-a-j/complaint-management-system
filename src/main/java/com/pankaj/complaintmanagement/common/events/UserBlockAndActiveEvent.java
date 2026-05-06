package com.pankaj.complaintmanagement.common.events;

import com.pankaj.complaintmanagement.common.enums.AccountStatus;

public record UserBlockAndActiveEvent (String recipientEmail, String username, String reason, AccountStatus status){
}
