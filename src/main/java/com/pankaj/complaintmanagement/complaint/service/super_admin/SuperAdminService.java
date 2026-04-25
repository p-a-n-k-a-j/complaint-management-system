package com.pankaj.complaintmanagement.complaint.service.super_admin;

import com.pankaj.complaintmanagement.auth.dto.RegisterRequest;
import com.pankaj.complaintmanagement.auth.repository.AuthRepository;
import com.pankaj.complaintmanagement.auth.service.AuthService;
import com.pankaj.complaintmanagement.common.enums.AccountStatus;
import com.pankaj.complaintmanagement.complaint.dto.AttachmentResponseDto;
import com.pankaj.complaintmanagement.complaint.dto.ComplaintResponseDTO;
import com.pankaj.complaintmanagement.complaint.repository.ComplaintAttachmentRepository;
import com.pankaj.complaintmanagement.complaint.repository.ComplaintRepository;
import com.pankaj.complaintmanagement.entity.Complaint;
import com.pankaj.complaintmanagement.entity.ComplaintAttachment;
import com.pankaj.complaintmanagement.entity.User;
import com.pankaj.complaintmanagement.entity.UserProfile;
import com.pankaj.complaintmanagement.exception.custom.UserAlreadyExistsException;
import com.pankaj.complaintmanagement.exception.custom.UserNotFoundException;
import com.pankaj.complaintmanagement.user.dto.UserDto;
import com.pankaj.complaintmanagement.user.repository.UserProfileRepository;
import com.pankaj.complaintmanagement.util.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminService {
    private final AuthRepository authRepository;
    private final UserProfileRepository profileRepository;
    private final ComplaintRepository complaintRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final ComplaintAttachmentRepository attachmentRepository;
    @Autowired
    public SuperAdminService(AuthRepository authRepository, UserProfileRepository profileRepository, ComplaintRepository complaintRepository, PasswordEncoder passwordEncoder, AuthService authService, ComplaintAttachmentRepository attachmentRepository) {
        this.authRepository = authRepository;
        this.profileRepository = profileRepository;
        this.complaintRepository = complaintRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
        this.attachmentRepository = attachmentRepository;
    }

    @Transactional
    public void register(RegisterRequest registerRequest){
        User user = authRepository.findByEmail(registerRequest.getEmail());

        if (user != null) {
            if (user.getStatus() == AccountStatus.ACTIVE) {
                throw new UserAlreadyExistsException("Email already in use!");
            }
            if (user.getStatus() == AccountStatus.DELETED) {
                authService.updateExistingUserWithNewData(user, registerRequest);
                return;
            }
            // Agar BLOCKED ya SUSPENDED hai
            throw new RuntimeException("Account is " + user.getStatus() + ". Contact admin.");
        }

        User newUser = new User();
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        /***
         * user role default hoga. or isse baad me sirf super admin change kar sakta hai, vo hi admin bna sakta hai*/
        newUser.setRoles(Set.of(UserRole.ROLE_USER, UserRole.ROLE_ADMIN));
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setStatus(AccountStatus.ACTIVE);
        authRepository.save(newUser);

        UserProfile profile = new UserProfile();
        profile.setFullName(registerRequest.getName());
        profile.setUser(newUser);
        profileRepository.save(profile);

    }


    @Transactional
    public User changeUserToAdmin(Long userId){
        User user = authRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setRoles(Set.of(UserRole.ROLE_USER, UserRole.ROLE_ADMIN));
       return user;

    }


    public Page<UserDto> getAllUser(int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("fullName").ascending());
        return authRepository.findAllByRolesWithProfile(UserRole.ROLE_USER, pageable).map(user -> mapUserTOUserDto(user, user.getUserProfile()));
    }

    public Page<UserDto> getAllAdmin(int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("fullName").ascending());
        return authRepository.findAllByRolesWithProfile(UserRole.ROLE_ADMIN, pageable).map(user -> mapUserTOUserDto(user, user.getUserProfile()));
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
                .imageUrl((userProfile.getImageUrl() !=null && !userProfile.getImageUrl().isBlank())? userProfile.getImageUrl(): null)
                .bio(userProfile.getBio())
                .publicId(userProfile.getPublicId())
                .pinCode(userProfile.getPinCode())
                .build();


    }
    public Page<ComplaintResponseDTO> getAllComplaintsByUserId(int page, int size, Long userId) {
        User user = authRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
       return complaintRepository.findByUser(user, pageable).map(this::mapToComplaintResponseDto);
    }
    public Page<ComplaintResponseDTO> getAssignedComplaintToAdmin(int page, int size, Long adminId) {
       User user = authRepository.findById(adminId).orElseThrow(()-> new UserNotFoundException("User not found"));
       Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return complaintRepository.findByAssignedTo(user, pageable).map(this::mapToComplaintResponseDto);
    }


    // ye hme duration nikal ke diga ki ek complaint ko resolve hone me kitna time lga.
    private String calculateResolutionDuration(LocalDateTime createdAt, LocalDateTime resolvedAt) {
        if (resolvedAt == null) return "Still In Progress";

        Duration duration = Duration.between(createdAt, resolvedAt);

        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();

        return String.format("%d Days, %d Hours, %d Minutes", days, hours, minutes);
    }

    private ComplaintResponseDTO mapToComplaintResponseDto(Complaint complaint){
        ComplaintResponseDTO dto = new ComplaintResponseDTO();

        //direct fields that are not causing null
        //complaint info
        List<ComplaintAttachment> byComplaint = attachmentRepository.findByComplaint(complaint);
        //direct fields that are not causing null
        //complaint info
        //here attachment work is done
        dto.setAttachments(byComplaint.stream()
                .map(attachment -> new AttachmentResponseDto.Builder()
                        .attachmentUrl(attachment.getAttachmentUrl())
                        .id(attachment.getId())
                        .publicId(attachment.getPublicId())
                        .complaintId(attachment.getComplaint().getId())
                        .build()).toList());
        dto.setCategory(complaint.getCategory());
        dto.setUpdatedAt(complaint.getUpdatedAt());
        dto.setCreatedAt(complaint.getCreatedAt());
        dto.setStatus(complaint.getStatus());
        dto.setId(complaint.getId());
        dto.setPriority(complaint.getPriority());
        dto.setTicketId(complaint.getTicketId());
        dto.setTitle(complaint.getTitle());
        dto.setDescription(complaint.getDescription());
        dto.setRemark(complaint.getRemark());
        dto.setResolvedAt(complaint.getResolvedAt());
        dto.setResolutionTime(calculateResolutionDuration(complaint.getCreatedAt(), complaint.getResolvedAt()));

        //user Info
        UserProfile profile = complaint.getUser().getUserProfile();
        dto.setName(profile.getFullName());
        dto.setImageUrl(profile.getImageUrl());
        dto.setEmail(complaint.getUser().getEmail());

        //Initial name logic null safe
        if(profile.getFullName() !=null && !profile.getFullName().isBlank()) {
            dto.setNameInitials(profile.getFullName().trim().substring(0, 1).toUpperCase());
        }
        //relationship safe
        dto.setUpdatedBy(Optional.ofNullable(complaint.getUpdatedBy())
                .map(User::getUserProfile)
                .map(UserProfile::getFullName)
                .orElse("N/A"));

        dto.setAssignedTo(Optional.ofNullable(complaint.getAssignedTo())
                .map(User::getUserProfile)
                .map(UserProfile::getFullName).orElse("Unassigned"));
        return dto;
    }


}