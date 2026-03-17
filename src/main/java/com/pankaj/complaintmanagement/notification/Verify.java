package com.pankaj.complaintmanagement.notification;

import java.util.HashMap;
import java.util.Map;

public class Verify {
    public static final Map<String, Boolean> isVerify = new HashMap<>();

    public static void markAsVerified(String email){
        isVerify.put(email, true);
    }

    public static boolean isVerified(String email){
        return isVerify.getOrDefault(email, false);
    }
    public static void clearVerification(String email){
        isVerify.remove(email);
    }
}
