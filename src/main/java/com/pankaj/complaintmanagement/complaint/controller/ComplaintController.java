package com.pankaj.complaintmanagement.complaint.controller;

import com.pankaj.complaintmanagement.common.response.ApiResponse;
import com.pankaj.complaintmanagement.complaint.dto.*;
import com.pankaj.complaintmanagement.complaint.service.ComplaintService;
import com.pankaj.complaintmanagement.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/complaint")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getMyComplaints(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "5")int size,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        Page<ComplaintResponseDTO> myComplaints = complaintService.getMyComplaints(page, size, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("successfully found", myComplaints));
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<ApiResponse<?>> getComplaintByTicketId(@PathVariable String ticketId, @AuthenticationPrincipal CustomUserDetails userDetails){
        ComplaintResponseDTO complaintByTicketId = complaintService.getComplaintByTicketId(ticketId, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("complaint found", complaintByTicketId));
    }
    @GetMapping("/log/{ticketId}")
    public ResponseEntity<ApiResponse<?>> getComplaintLogsByTicketId(@PathVariable String ticketId, @AuthenticationPrincipal CustomUserDetails userDetails){
        List<ComplaintLogResponseDTO> logsByTicketId = complaintService.getLogsByTicketId(ticketId, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("Logs are found by TicketId #"+ ticketId, logsByTicketId));
    }

    @GetMapping("/history/{complaintId}")
    public ResponseEntity<ApiResponse<?>> getComplaintHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @PathVariable Long complaintId, @AuthenticationPrincipal CustomUserDetails userDetails){
        Page<ComplaintLogResponseDTO> complaintHistory = complaintService.getComplaintHistory(page, size, complaintId, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("found complaint history of #"+complaintId, complaintHistory));
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<?>> getTodayComplaintUpdates(@AuthenticationPrincipal CustomUserDetails userDetails){
        List<ComplaintResponseDTO> todayUpdatesForUser = complaintService.getTodayUpdatesForUser(userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("successfully found Today updates", todayUpdatesForUser));
    }



    @PostMapping
    public ResponseEntity<ApiResponse<?>> createComplaint(@RequestBody ComplaintRequest request, @AuthenticationPrincipal CustomUserDetails userDetails){
        complaintService.createComplaint(request, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Complaint created successfully"));
    }
    @PostMapping(value = "/update-attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> updateAttachment(
            @RequestParam("complaintId") Long complaintId,
            @RequestParam("files") List<MultipartFile> files,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        complaintService.updateAttachments(complaintId, files, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("Successfully updated all attachments"));
    }

    @PostMapping(value = "/add-attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> addAttachment(
            @RequestParam("complaintId") Long complaintId,
            @RequestParam("files") List<MultipartFile> files,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        complaintService.addAttachments(complaintId, files, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("Successfully added attachments to existing files"));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<?>> partialUpdateComplaint(@RequestBody ComplaintRequest request, @AuthenticationPrincipal CustomUserDetails userDetails){
        complaintService.partialUpdateComplaint(request, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("Complaint updated successfully"));
    }

    @DeleteMapping("/{complaintId}")
    public ResponseEntity<ApiResponse<?>> deleteComplaint(@PathVariable Long complaintId, @AuthenticationPrincipal CustomUserDetails userDetails ){
        complaintService.deleteComplaint(complaintId, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("Complaint deleted successfully"));
    }
    @PutMapping
    public ResponseEntity<ApiResponse<?>> UpdateComplaint(@RequestBody @Valid ComplaintUpdateRequest updateRequest, @AuthenticationPrincipal CustomUserDetails userDetails){
        complaintService.updateComplaint(updateRequest, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("Complaint updated successfully"));
    }



}
