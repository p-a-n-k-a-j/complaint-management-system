package com.pankaj.complaintmanagement.complaint.controller;

import com.pankaj.complaintmanagement.common.response.ApiResponse;
import com.pankaj.complaintmanagement.complaint.dto.ComplaintResponseDTO;
import com.pankaj.complaintmanagement.complaint.dto.StatusChangeRequest;
import com.pankaj.complaintmanagement.complaint.dto.admin.SetRemarkRequest;
import com.pankaj.complaintmanagement.complaint.service.ComplaintService;
import com.pankaj.complaintmanagement.notification.EmailService;
import com.pankaj.complaintmanagement.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/admin/complaints")
public class AdminComplaintController {
    private final ComplaintService complaintService;
    private final EmailService emailService;
    @Autowired
    public AdminComplaintController(ComplaintService complaintService, EmailService emailService) {
        this.complaintService = complaintService;
        this.emailService = emailService;
    }

    @GetMapping("/assigned")
    public ResponseEntity<ApiResponse<?>> getMyAssignedComplaint(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "5") int size,
                                                                 @AuthenticationPrincipal CustomUserDetails userDetails){
        Page<ComplaintResponseDTO> myAssignedComplaint = complaintService.getMyAssignedComplaint(page, size, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("Assigned complaint found", myAssignedComplaint));
    }

    @GetMapping("/updates")
    public ResponseEntity<ApiResponse<?>> getTodayUpdates(@AuthenticationPrincipal CustomUserDetails userDetails){
        List<ComplaintResponseDTO> todayUpdatesAssignedAdmin = complaintService.getTodayUpdatesAssignedAdmin(userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("Today's Updates", todayUpdatesAssignedAdmin));
    }

    @PostMapping("/remark")
    public ResponseEntity<ApiResponse<?>> setRemark(@RequestBody @Valid SetRemarkRequest request, @AuthenticationPrincipal CustomUserDetails userDetails){
        ComplaintResponseDTO dto = complaintService.setRemarkToComplaint(request.getComplaintId(), request.getRemark(), userDetails.getUser());
        emailService.sendRemarkChangeEmail(dto);
        return ResponseEntity.ok(ApiResponse.success("Remark set successfully to #"+ request.getComplaintId()));
    }


    @PatchMapping("/status")
    public ResponseEntity<ApiResponse<?>> updateStatus(@RequestBody @Valid StatusChangeRequest request, @AuthenticationPrincipal CustomUserDetails userDetails){
        ComplaintResponseDTO dto = complaintService.updateComplaintStatus(request.getComplaintId(), request.getStatus(), request.getRemark(), userDetails.getUser());
        emailService.sendComplaintStatusUpdateEmail(dto);
     return ResponseEntity.ok(ApiResponse.success("Status changed successfully."));
    }



}
