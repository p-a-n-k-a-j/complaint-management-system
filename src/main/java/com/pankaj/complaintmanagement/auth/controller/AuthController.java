package com.pankaj.complaintmanagement.auth.controller;

import com.pankaj.complaintmanagement.auth.dto.LoginRequest;
import com.pankaj.complaintmanagement.auth.dto.RegisterRequest;
import com.pankaj.complaintmanagement.auth.service.AuthService;
import com.pankaj.complaintmanagement.common.response.ApiResponse;
import com.pankaj.complaintmanagement.notification.EmailService;
import com.pankaj.complaintmanagement.notification.OtpService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final AuthService authService;
    private EmailService emailService;
    private OtpService otpService;
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
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "login is successfully done", accessTokenAndRefreshToken));
    }
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(){
        return null;
    }
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request){
        emailService.sendOtpEmail(request.get("email"));

    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request){
        otpService.verifyOtp(request.get("email"), Integer.parseInt(request.get("otp")));
    }
}
