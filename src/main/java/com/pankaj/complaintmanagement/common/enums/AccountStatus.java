package com.pankaj.complaintmanagement.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public enum AccountStatus {
    ACTIVE, BLOCKED, SUSPENDED, DELETED
}
