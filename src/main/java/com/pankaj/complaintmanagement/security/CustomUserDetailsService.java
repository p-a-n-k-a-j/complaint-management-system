package com.pankaj.complaintmanagement.security;

import com.pankaj.complaintmanagement.auth.repository.AuthRepository;
import com.pankaj.complaintmanagement.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    private AuthRepository authRepository;
    public CustomUserDetailsService(AuthRepository authRepository){
        this.authRepository = authRepository;
    }
    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = authRepository.findByEmail(username);
        if(user ==null){
            throw new UsernameNotFoundException("Username not found");
        }
        return new CustomUserDetails(user);
    }
}
