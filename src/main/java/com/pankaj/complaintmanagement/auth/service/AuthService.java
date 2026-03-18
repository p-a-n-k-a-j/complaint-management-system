package com.pankaj.complaintmanagement.auth.service;

import com.pankaj.complaintmanagement.auth.dto.RegisterRequest;
import com.pankaj.complaintmanagement.auth.repository.AuthRepository;
import com.pankaj.complaintmanagement.entity.User;
import com.pankaj.complaintmanagement.exception.custom.UserAlreadyExistsException;
import com.pankaj.complaintmanagement.notification.EmailService;
import com.pankaj.complaintmanagement.util.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class AuthService {
    private final AuthRepository authRepository;
    private final EmailService emailService;

    public AuthService(AuthRepository authRepository,EmailService emailService) {
        this.authRepository = authRepository;
        this.emailService = emailService;
    }
    @Transactional
    public void register(RegisterRequest registerRequest){
        User user = authRepository.findByEmail(registerRequest.getEmail());

        if(user !=null){throw new UserAlreadyExistsException("Username already  exists with this email");}
        User newUser = new User();
        newUser.setName(registerRequest.getName());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(registerRequest.getPassword());
        /***
         * user role default hoga. or isse baad me sirf super admin change kar sakta hai, vo hi admin bna sakta hai*/
        newUser.setRoles(Set.of(UserRole.ROLE_USER));
        newUser.setCreatedAt(LocalDateTime.now());
        authRepository.save(newUser);

    }
}
