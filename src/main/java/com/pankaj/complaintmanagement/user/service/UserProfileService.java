package com.pankaj.complaintmanagement.user.service;

import com.pankaj.complaintmanagement.auth.repository.AuthRepository;
import com.pankaj.complaintmanagement.entity.User;
import com.pankaj.complaintmanagement.entity.UserProfile;
import com.pankaj.complaintmanagement.exception.custom.UserNotFoundException;
import com.pankaj.complaintmanagement.exception.custom.UserProfileNotFoundException;
import com.pankaj.complaintmanagement.user.dto.UpdateProfileRequest;
import com.pankaj.complaintmanagement.user.dto.UserDto;
import com.pankaj.complaintmanagement.user.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final AuthRepository authRepository;
    @Autowired
    public UserProfileService(AuthRepository authRepository, UserProfileRepository userProfileRepository){
       this.authRepository = authRepository;
        this.userProfileRepository = userProfileRepository;
    }

    public UserDto getUser(Long id) {
        User user = authRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        UserProfile userProfile = userProfileRepository.findByUser(user).orElseThrow(() -> new UserProfileNotFoundException("user profile not found"));
        return mapUserTOUserDto(user, userProfile);

    }
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public List<UserDto> getAllUser(){
        return authRepository.findAll()
                .stream().map(user ->{
                    UserProfile userProfile = userProfileRepository.findByUser(user)
                            .orElseThrow(() -> new UserProfileNotFoundException("user profile not found"));
                    return mapUserTOUserDto(user, userProfile);
                }).toList();
    }

    private UserDto mapUserTOUserDto(User user, UserProfile userProfile){
        return new UserDto.Builder()
                .id(user.getId())
                .name(userProfile.getFullName())
                .email(user.getEmail())
                .address(userProfile.getAddress())
                .city(userProfile.getCity())
                .phone(userProfile.getPhone())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .updatedAt(userProfile.getLastUpdate())
                .state(userProfile.getState())
                .status(user.getStatus())
                .imageUrl(userProfile.getImageUrl())
                .bio(userProfile.getBio())
                .build();


    }

    public void updateUserProfile(UpdateProfileRequest profileRequest, User user) {
        UserProfile userProfile =userProfileRepository.findByUser(user).orElseThrow(() -> new UserProfileNotFoundException("user Profile not found"));
        userProfile.setFullName(profileRequest.getName());
        userProfile.setCity(profileRequest.getCity());
        userProfile.setAddress(profileRequest.getAddress());
        userProfile.setImageUrl(profileRequest.getImageUrl());
        userProfile.setLastUpdate(LocalDateTime.now());
        userProfile.setPhone(profileRequest.getPhone());
        userProfile.setPincode(profileRequest.getPincode());
        userProfile.setState(profileRequest.getState());
        userProfile.setBio(profileRequest.getBio());
        userProfileRepository.save(userProfile);
    }
}
