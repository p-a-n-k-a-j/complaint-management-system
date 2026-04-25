package com.pankaj.complaintmanagement.auth.controller;

import com.pankaj.complaintmanagement.auth.dto.*;
import com.pankaj.complaintmanagement.auth.service.AuthService;
import com.pankaj.complaintmanagement.common.enums.AccountStatus;
import com.pankaj.complaintmanagement.common.response.ApiResponse;
import com.pankaj.complaintmanagement.notification.EmailService;
import com.pankaj.complaintmanagement.notification.OtpService;
import com.pankaj.complaintmanagement.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final AuthService authService;
    private final EmailService emailService;
    private final OtpService otpService;
    @Autowired
    AuthController(EmailService emailService, OtpService otpService, AuthService authService){
        this.emailService = emailService;
        this.otpService =otpService;
        this.authService = authService;
    }


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> registerAccount(@RequestBody @Valid RegisterRequest registerRequest){
    authService.register(registerRequest);
    emailService.sendRegistrationEmail(registerRequest.getEmail(), registerRequest.getName());
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("User created successfully"));
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody @Valid LoginRequest loginRequest){
        Map<String, String> accessTokenAndRefreshToken =authService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("login is successfully done", accessTokenAndRefreshToken));
    }
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(@AuthenticationPrincipal CustomUserDetails userDetails){
        authService.logout(userDetails.getId());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("user successfully logout"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refreshToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest){
        Map<String, String> accessTokenAndRefreshToken = authService.refresh(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Token refreshed successfully", accessTokenAndRefreshToken));
    }
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request){
        emailService.sendOtpEmail(request.get("email"));
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Otp Sent"));

    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request){
        otpService.verifyOtp(request.get("email"), Integer.parseInt(request.get("otp")));
        return ResponseEntity.ok(ApiResponse.success("email verified successfully"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody @Valid ForgotPasswordRequest passwordRequest){
        authService.forgotPassword(passwordRequest.getEmail(), passwordRequest.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("Password successfully updated"));
    }



}
