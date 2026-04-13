package com.pankaj.complaintmanagement.complaint.service.impl;

import com.pankaj.complaintmanagement.auth.repository.AuthRepository;
import com.pankaj.complaintmanagement.common.enums.ComplaintStatus;
import com.pankaj.complaintmanagement.common.services.CloudinaryService;
import com.pankaj.complaintmanagement.complaint.dto.ComplaintLogDTO;
import com.pankaj.complaintmanagement.complaint.dto.ComplaintRequest;
import com.pankaj.complaintmanagement.complaint.dto.ComplaintResponseDTO;
import com.pankaj.complaintmanagement.complaint.repository.ComplaintLogRepository;
import com.pankaj.complaintmanagement.complaint.repository.ComplaintRepository;
import com.pankaj.complaintmanagement.complaint.service.ComplaintService;
import com.pankaj.complaintmanagement.entity.*;
import com.pankaj.complaintmanagement.exception.custom.ComplaintNotFoundException;
import com.pankaj.complaintmanagement.exception.custom.UserNotFoundException;
import com.pankaj.complaintmanagement.exception.custom.UserProfileNotFoundException;
import com.pankaj.complaintmanagement.user.repository.UserProfileRepository;
import com.pankaj.complaintmanagement.util.ComplaintCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ComplaintServiceImpl implements ComplaintService {
   private final AuthRepository authRepository;
   private final ComplaintRepository complaintRepository;
   private final ComplaintLogRepository complaintLogRepository;
   private final UserProfileRepository userProfileRepository;
   private final CloudinaryService cloudinaryService;
   @Autowired
    public ComplaintServiceImpl(AuthRepository authRepository, ComplaintRepository complaintRepository, ComplaintLogRepository complaintLogRepository, UserProfileRepository userProfileRepository, CloudinaryService cloudinaryService) {
        this.authRepository = authRepository;
        this.complaintRepository = complaintRepository;
        this.complaintLogRepository = complaintLogRepository;
        this.userProfileRepository = userProfileRepository;
       this.cloudinaryService = cloudinaryService;
   }

    @Override
    public void createComplaint(ComplaintRequest request, User user) {
        User foundUser =authRepository.findById(user.getId()).orElseThrow(()-> new UserNotFoundException("user not found"));
        UserProfile profile = userProfileRepository.findByUser(foundUser).orElseThrow(()-> new UserProfileNotFoundException("user profile not found"));
      String ticketId="";
        do{
            ticketId = generateTicketId();
        }while (complaintRepository.existsByTicketId(ticketId));

        Complaint complaint = new Complaint();
        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());
        complaint.setRemark((request.getRemark() !=null )? request.getRemark():"Complaint Registered");
        complaint.setCategory(request.getCategory());
        complaint.setStatus(ComplaintStatus.PENDING);
        complaint.setCreatedAt(LocalDateTime.now());
        complaint.setActionBy(profile.getFullName());
        complaint.setTicketId(ticketId);
        complaint.setUser(foundUser);
        complaint.setPriority(request.getPriority());

       setAttachment(complaint, request.getAttachments());

        this.saveLog(complaint, null, ComplaintStatus.PENDING);

        complaintRepository.save(complaint);

    }
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    @Override
    public void updateComplaintStatus(Long complaintId, ComplaintStatus newStatus, String remark, User admin) {
       User foundUser = authRepository.findById(admin.getId()).orElseThrow(()-> new UserNotFoundException("user not found"));
       UserProfile profile = userProfileRepository.findByUser(foundUser).orElseThrow(()-> new UserProfileNotFoundException("user profile not found"));
    Complaint complaint =  complaintRepository.findById(complaintId).orElseThrow(()-> new ComplaintNotFoundException("complaint not found"));
    ComplaintStatus previousStatus = complaint.getStatus();
    complaint.setStatus(newStatus);
    if(remark != null){
        complaint.setRemark(remark);
    }
    if(newStatus == ComplaintStatus.RESOLVED){
        complaint.setResolvedAt(LocalDateTime.now());
    }
    complaint.setUpdatedAt(LocalDateTime.now());
    this.saveLog(complaint, previousStatus, newStatus);
    }

    @Override
    public void deleteComplaint(Long complaintId) {

    }

    @Override
    public Page<ComplaintResponseDTO> getMyComplaints(int page, int size, User user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("").ascending());
       Page<Complaint> complaintPage= complaintRepository.findByUser(user, pageable);
        return null;
    }

    @Override
    public Page<ComplaintResponseDTO> getAllComplaints(int page, int size, ComplaintStatus status, ComplaintCategory category) {
        return null;
    }

    @Override
    public List<ComplaintLogDTO> getComplaintHistory(String ticketId) {
        return List.of();
    }

    @Override
    public ComplaintResponseDTO getComplaintByTicketId(String ticketId) {
        return null;
    }

    @Override
    public void updateComplaint(ComplaintRequest request, User user) {

    }

    private String generateTicketId(){
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));
        int random = new SecureRandom().nextInt(9000)+1000;
        return "CMS"+datePart+random;
    }
    private ComplaintResponseDTO mapToDto(Complaint complaint){
return null;
    }

    private void saveLog(Complaint complaint,ComplaintStatus previousStatus, ComplaintStatus newStatus){
        ComplaintLog log = new ComplaintLog();
        log.setPreviousStatus(previousStatus);
        log.setComplaint(complaint);
        log.setRemark((complaint.getRemark() !=null)? complaint.getRemark() : previousStatus + " Status changed to "+ newStatus);
        log.setTicketId(complaint.getTicketId());
        log.setActionBy(complaint.getActionBy());
        log.setLogTime(LocalDateTime.now());
        log.setNewStatus(newStatus);
        complaint.getComplaintLogs().add(log);
    }
    //this is responsible to handle all Complaint attachment
    private List<ComplaintAttachment> setAttachment(Complaint complaint, List<MultipartFile> files){
        if (files == null || files.isEmpty()) return new ArrayList<>();
        List<ComplaintAttachment> attachmentList = files.stream().map(file -> {
            Map upload = cloudinaryService.upload(file);
            ComplaintAttachment complaintAttachment = new ComplaintAttachment();
            complaintAttachment.setComplaint(complaint);
            complaintAttachment.setAttachmentUrls(upload.get("secure_url").toString());
            complaintAttachment.setPublicId(upload.get("public_id").toString());
            return complaintAttachment;
        }).toList();
        complaint.getComplaintAttachment().addAll(attachmentList);
        return attachmentList;
    }
}
