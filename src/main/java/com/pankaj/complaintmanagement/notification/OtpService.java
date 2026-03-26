package com.pankaj.complaintmanagement.notification;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class OtpService {

    private static final Map<String, Integer> cacheOfOtp = new HashMap<>();

    //these three methods to automate the work of verify otp
    //how: when sendEmail call then saveOtp called then otp is in cacheOfOtp
    // then I directly getOtp from the cacheOfOtp don't need to manually put in method parameter.
    // programmer only needs to call sendOtp method everything did.
    // when verifying, we don't need to extract the otp from sendOtp method to verify
    // we only give email and rawOtp it will automatically do and markAsVerified.
    public void saveOtp(String email, int generatedOtp){
        cacheOfOtp.put(email, generatedOtp);
    }
    public int getStoredOtp(String email){
        return cacheOfOtp.getOrDefault(email, 0);
    }

    public void cleanStoredOtp(String email){
        cacheOfOtp.remove(email);
    }
// this responsible for generation otp
    public int generateOtp(){
        SecureRandom rand = new SecureRandom();
        return 100000 + rand.nextInt(900000);
    }


    //here is a verification method
    public boolean verifyOtp(String email , int otp){
        int storedOtp = getStoredOtp(email);
        if (storedOtp == 0) {
            throw new BadCredentialsException("OTP expired or not found");
        }
        if(Objects.equals(otp, storedOtp)){
            cleanStoredOtp(email);
            Verify.markAsVerified(email);
           return true;
        }
        throw new BadCredentialsException("Wrong otp entered");
    }



}
