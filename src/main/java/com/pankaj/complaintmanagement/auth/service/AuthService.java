package com.pankaj.complaintmanagement.auth.service;

import com.pankaj.complaintmanagement.auth.dto.RegisterRequest;
import com.pankaj.complaintmanagement.auth.repository.AuthRepository;
import com.pankaj.complaintmanagement.entity.User;
import com.pankaj.complaintmanagement.exception.custom.EmailNotVerifiedException;
import com.pankaj.complaintmanagement.exception.custom.UserAlreadyExistsException;
import com.pankaj.complaintmanagement.notification.Verify;
import com.pankaj.complaintmanagement.security.CustomUserDetails;
import com.pankaj.complaintmanagement.security.JwtService;
import com.pankaj.complaintmanagement.util.UserRole;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
public class AuthService {
    private final AuthRepository authRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    @Autowired
    public AuthService(AuthRepository authRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.authRepository = authRepository;

        this.passwordEncoder=passwordEncoder;
        this.jwtService = jwtService;
    }
    @Transactional
    public void register(RegisterRequest registerRequest){
        User user = authRepository.findByEmail(registerRequest.getEmail());

        if(user !=null){throw new UserAlreadyExistsException("Username already  exists with this email");}
       if(!Verify.isVerified(registerRequest.getEmail())){
           throw new EmailNotVerifiedException("Email is not verified");
       }

        User newUser = new User();
        newUser.setName(registerRequest.getName());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        /***
         * user role default hoga. or isse baad me sirf super admin change kar sakta hai, vo hi admin bna sakta hai*/
        newUser.setRoles(Set.of(UserRole.ROLE_USER));
        newUser.setCreatedAt(LocalDateTime.now());
        authRepository.save(newUser);
        Verify.clearVerification(registerRequest.getEmail());

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
            user.setRefreshToken(refreshToken);
            user.setActive(true);
            authRepository.save(user);
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        }
        throw new BadCredentialsException("Invalid credentials");
    }
//this is for refresh both access and refresh token
    public Map<String, String> refresh(String refreshToken) {
        Claims claims = jwtService.extractAllClaims(refreshToken);

        if(!jwtService.isValidRefreshToken(claims)){
            throw new BadCredentialsException("Invalid refresh token");
        }
        String username = jwtService.extractUsername(claims);
        User user = authRepository.findByEmail(username);
        if(user ==null)throw new UsernameNotFoundException("username not found");


        if(!Objects.equals(refreshToken, user.getRefreshToken())) {
            throw new BadCredentialsException("Invalid token");
        }

            String accessToken = jwtService.accessToken(new CustomUserDetails(user));
            String refreshedToken = jwtService.refreshToken(user.getEmail());
            user.setRefreshToken(refreshedToken);
            authRepository.save(user);
            return Map.of("accessToken", accessToken, "refreshToken", refreshedToken);
    }
@Transactional
    public void forgotPassword(String email, String newPassword) {

        User user = authRepository.findByEmail(email);
        if(user == null) throw new UsernameNotFoundException("User not found");

        if(!Verify.isVerified(email)){
            throw new EmailNotVerifiedException("Email is not verified");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        authRepository.save(user);

        Verify.clearVerification(email); // one-time use
    }
@Transactional
    public void logout(Long id) {
        User user = authRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("user not found"));
        user.setRefreshToken(null);

    }
}
