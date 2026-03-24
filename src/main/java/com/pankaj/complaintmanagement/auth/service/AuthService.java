package com.pankaj.complaintmanagement.auth.service;

import com.pankaj.complaintmanagement.auth.dto.RegisterRequest;
import com.pankaj.complaintmanagement.auth.repository.AuthRepository;
import com.pankaj.complaintmanagement.entity.User;
import com.pankaj.complaintmanagement.exception.custom.UserAlreadyExistsException;
import com.pankaj.complaintmanagement.exception.custom.UsernameNotFoundException;
import com.pankaj.complaintmanagement.notification.EmailService;
import com.pankaj.complaintmanagement.security.CustomUserDetails;
import com.pankaj.complaintmanagement.security.CustomUserDetailsService;
import com.pankaj.complaintmanagement.security.JwtService;
import com.pankaj.complaintmanagement.util.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Service
public class AuthService {
    private final AuthRepository authRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    @Autowired
    public AuthService(AuthRepository authRepository, EmailService emailService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.authRepository = authRepository;
        this.emailService = emailService;
        this.passwordEncoder=passwordEncoder;
        this.jwtService = jwtService;
    }
    @Transactional
    public void register(RegisterRequest registerRequest){
        User user = authRepository.findByEmail(registerRequest.getEmail());

        if(user !=null){throw new UserAlreadyExistsException("Username already  exists with this email");}
        User newUser = new User();
        newUser.setName(registerRequest.getName());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        /***
         * user role default hoga. or isse baad me sirf super admin change kar sakta hai, vo hi admin bna sakta hai*/
        newUser.setRoles(Set.of(UserRole.ROLE_USER));
        newUser.setCreatedAt(LocalDateTime.now());
        authRepository.save(newUser);

    }

    public Map<String, String> login(String email, String rawPassword) {

        User user = authRepository.findByEmail(email);
        if(user ==null){
            throw new UsernameNotFoundException("Username not found");
        }
        if(passwordEncoder.matches(rawPassword, user.getPassword())){
            CustomUserDetails userDetails = new CustomUserDetails(user);
            String accessToken = jwtService.accessToken(userDetails);
            String refreshToken = jwtService.refreshToken(user.getEmail());
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        }
        throw new BadCredentialsException("Invalid credentials");
    }
}
