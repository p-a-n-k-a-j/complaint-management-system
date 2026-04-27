package com.pankaj.complaintmanagement.complaint.controller;

import com.pankaj.complaintmanagement.auth.dto.RegisterRequest;
import com.pankaj.complaintmanagement.auth.service.AuthService;
import com.pankaj.complaintmanagement.common.enums.ComplaintStatus;
import com.pankaj.complaintmanagement.common.response.ApiResponse;
import com.pankaj.complaintmanagement.complaint.dto.ComplaintResponseDTO;
import com.pankaj.complaintmanagement.complaint.dto.StatusChangeRequest;
import com.pankaj.complaintmanagement.complaint.dto.admin.SetRemarkRequest;
import com.pankaj.complaintmanagement.complaint.dto.super_admin.StatusUpdateRequest;
import com.pankaj.complaintmanagement.complaint.service.ComplaintService;
import com.pankaj.complaintmanagement.complaint.service.super_admin.SuperAdminService;
import com.pankaj.complaintmanagement.entity.Complaint;
import com.pankaj.complaintmanagement.entity.User;
import com.pankaj.complaintmanagement.notification.EmailService;
import com.pankaj.complaintmanagement.security.CustomUserDetails;
import com.pankaj.complaintmanagement.user.dto.UserDto;
import com.pankaj.complaintmanagement.user.service.UserProfileService;
import com.pankaj.complaintmanagement.util.ComplaintCategory;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/super-admin")
public class SuperAdminComplaintController {
    private final ComplaintService complaintService;
    private final EmailService emailService;
    private final AuthService authService;
    private final SuperAdminService superAdminService;
    @Autowired
    public SuperAdminComplaintController(ComplaintService complaintService, EmailService emailService, AuthService authService, SuperAdminService superAdminService) {
        this.complaintService = complaintService;
        this.emailService = emailService;
        this.authService = authService;
        this.superAdminService = superAdminService;
    }

    // ==========================================
    // 2. COMPLAINT MANAGEMENT (Operations)
    // ==========================================

    @GetMapping("/complaints/recent")
    public ResponseEntity<ApiResponse<?>> getTodayUpdate(){
        List<ComplaintResponseDTO> todayUpdates = complaintService.getTodayUpdates();
        return ResponseEntity.ok(ApiResponse.success("Today's updates ", todayUpdates));
    }
    @GetMapping("/complaints/all")
    public ResponseEntity<ApiResponse<?>> getAllComplaints(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) ComplaintStatus status,
            @RequestParam(required = false) ComplaintCategory category
            ){
        Page<ComplaintResponseDTO> allComplaintsForSuperAdmin = complaintService.getAllComplaintsForSuperAdmin(page, size, status, category);
        return ResponseEntity.ok(ApiResponse.success("All Complaints are here", allComplaintsForSuperAdmin));
    }

    @GetMapping("/complaints/user/{userId}")
    public ResponseEntity<ApiResponse<?>> getComplaintsByUserId(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "5")int size,
            @PathVariable Long userId){
        Page<ComplaintResponseDTO> allUserComplaints = superAdminService.getAllComplaintsByUserId(page, size,userId);
        return ResponseEntity.ok(ApiResponse.success("Successfully found complaints of:#"+userId, allUserComplaints));
    }


    @PostMapping("/complaints/{complaintId}/assign/admin/{adminId}")
    public ResponseEntity<ApiResponse<?>> assignAdmin(@PathVariable Long complaintId, @PathVariable Long adminId, @AuthenticationPrincipal CustomUserDetails userDetails){
        Complaint complaint = complaintService.assignTo(complaintId, adminId, userDetails.getUser());
        emailService.sendAssignmentEmail(complaint);
        return ResponseEntity.ok(ApiResponse.success("complaint assigned to: "+complaint.getAssignedTo().getUserProfile().getFullName()));
    }

    @PatchMapping("/complaints/remark")
    public ResponseEntity<ApiResponse<?>> setRemark(@RequestBody @Valid SetRemarkRequest request, @AuthenticationPrincipal CustomUserDetails userDetails){
        ComplaintResponseDTO dto = complaintService.setRemarkToComplaint(request.getComplaintId(), request.getRemark(), userDetails.getUser());
        emailService.sendRemarkChangeEmail(dto);
        return ResponseEntity.ok(ApiResponse.success("Remark successfully set"));
    }
    @GetMapping("/complaints/admin/{adminId}")
    public ResponseEntity<ApiResponse<?>> getAssignedComplaint(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "5") int size,
                                                                 @PathVariable Long adminId){
        Page<ComplaintResponseDTO> myAssignedComplaint = superAdminService.getAssignedComplaintToAdmin(page, size, adminId);
        return ResponseEntity.ok(ApiResponse.success("Assigned complaint found", myAssignedComplaint));
    }
    @PatchMapping("/complaints/status")
    public ResponseEntity<ApiResponse<?>> changeStatus(@RequestBody @Valid StatusChangeRequest request, @AuthenticationPrincipal CustomUserDetails userDetails){
        ComplaintResponseDTO dto = complaintService.updateComplaintStatus(request.getComplaintId(), request.getStatus(), request.getRemark(), userDetails.getUser());
        emailService.sendComplaintStatusUpdateEmail(dto);
        return ResponseEntity.ok(ApiResponse.success("Status updated to: "+ dto.getStatus()));
    }

    // ==========================================
    // 3. MONITORING & ANALYTICS (Insights)
    // ==========================================

    //here monitoring other admin work started or user as well
    @GetMapping("/monitor/admin/{adminId}/stats")
    public ResponseEntity<ApiResponse<?>> getAdminStats(@PathVariable Long adminId){
        Map<String, Long> adminStats = complaintService.getAdminStats(adminId);
        return ResponseEntity.ok(ApiResponse.success("Admin stats are present", adminStats));
    }
    @GetMapping("/monitor/user/{userId}/stats")
    public ResponseEntity<ApiResponse<?>> getUserStats(@PathVariable Long userId){
        Map<String, Long> userStats = complaintService.getUserStats(userId);
        return ResponseEntity.ok(ApiResponse.success("User stats are present", userStats));
    }

    @PatchMapping("/monitor/user/{userId}/status") // Path mein sirf ID rakho
    public ResponseEntity<ApiResponse<?>> changeUserAccountStatus(
            @PathVariable Long userId,
            @RequestBody StatusUpdateRequest request, // Status body mein lo
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        authService.changeStatus(userId, request.getStatus(), userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("User Account status changed to: " + request.getStatus()));
    }

    // ==========================================
    // 1. USER & ADMIN MANAGEMENT (Governance)
    // ==========================================

    @GetMapping("/all/user/profiles")
    public ResponseEntity<ApiResponse<?>> getAllUserProfile(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "5") int size
    ){
        Page<UserDto> usersProfiles = superAdminService.getAllUser(page, size);
        return ResponseEntity.ok(ApiResponse.success("User profiles found", usersProfiles));
    }
    @GetMapping("/all/admin/profiles")
    public ResponseEntity<ApiResponse<?>> getAllAdminProfile(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "5") int size
    ){
        Page<UserDto> adminProfiles = superAdminService.getAllAdmin(page, size);
        return ResponseEntity.ok(ApiResponse.success("Admin profiles found", adminProfiles));
    }



    @PatchMapping("/user/{userId}/into-admin")
    public ResponseEntity<ApiResponse<?>> changeUserToAdmin(@PathVariable Long userId){
        User user = superAdminService.changeUserToAdmin(userId);
        emailService.sendAdminPromotionEmail(user.getEmail(), user.getUserProfile().getFullName());
        return ResponseEntity.ok(ApiResponse.success("Now user is Admin as well"));
    }

    @PostMapping("/register/admin")
    public ResponseEntity<ApiResponse<?>> registerNewAdmin(@RequestBody RegisterRequest registerRequest){
        superAdminService.register(registerRequest);
        emailService.sendRegistrationEmailToAdmin(registerRequest.getEmail(), registerRequest.getName(), registerRequest.getPassword());

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("New Admin account is created successfully"));
    }











}
