package com.pankaj.complaintmanagement.complaint.service.impl;

import com.pankaj.complaintmanagement.auth.repository.AuthRepository;
import com.pankaj.complaintmanagement.common.enums.ComplaintStatus;
import com.pankaj.complaintmanagement.common.services.CloudinaryService;
import com.pankaj.complaintmanagement.common.services.WebSocketService;
import com.pankaj.complaintmanagement.complaint.dto.*;
import com.pankaj.complaintmanagement.complaint.repository.ComplaintLogRepository;
import com.pankaj.complaintmanagement.complaint.repository.ComplaintRepository;
import com.pankaj.complaintmanagement.complaint.service.ComplaintService;
import com.pankaj.complaintmanagement.entity.*;
import com.pankaj.complaintmanagement.exception.custom.ComplaintNotFoundException;
import com.pankaj.complaintmanagement.exception.custom.UnauthorizedActionException;
import com.pankaj.complaintmanagement.exception.custom.UserNotFoundException;
import com.pankaj.complaintmanagement.exception.custom.UserProfileNotFoundException;
import com.pankaj.complaintmanagement.user.repository.UserProfileRepository;
import com.pankaj.complaintmanagement.util.ComplaintCategory;
import com.pankaj.complaintmanagement.util.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ComplaintServiceImpl implements ComplaintService {
   private final AuthRepository authRepository;
   private final ComplaintRepository complaintRepository;
   private final ComplaintLogRepository complaintLogRepository;
   private final UserProfileRepository userProfileRepository;
   private final CloudinaryService cloudinaryService;
   private final WebSocketService webSocketService;
   @Autowired
    public ComplaintServiceImpl(AuthRepository authRepository, ComplaintRepository complaintRepository, ComplaintLogRepository complaintLogRepository, UserProfileRepository userProfileRepository, CloudinaryService cloudinaryService, WebSocketService webSocketService) {
        this.authRepository = authRepository;
        this.complaintRepository = complaintRepository;
        this.complaintLogRepository = complaintLogRepository;
        this.userProfileRepository = userProfileRepository;
       this.cloudinaryService = cloudinaryService;
       this.webSocketService = webSocketService;
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
        complaint.setUpdatedBy(user);
        complaint.setTicketId(ticketId);
        complaint.setUser(foundUser);
        complaint.setPriority(request.getPriority());


        this.saveLog(complaint, null, ComplaintStatus.PENDING);

        complaintRepository.save(complaint);

    }
    //TODO: here are present those two spacial method that are handle attachments for user
    @Transactional
    @Override
    public void updateAttachments(Long complaintId, List<MultipartFile> files, User user) {
        Complaint complaint = complaintRepository.findById(complaintId).orElseThrow(() -> new ComplaintNotFoundException("Complaint not found"));

        if(!Objects.equals(complaint.getUser().getId(), user.getId())){
            throw new UnauthorizedActionException("Sorry! you are not authorized user to take this action");
        }
        //now time to set new attachments
        //the set attachment: first remove all previous or junk files and add new one
        complaint.setComplaintAttachment(this.updateAttachments(complaint, files));

    }
    @Transactional
    @Override
    public void addAttachments(Long complaintId, List<MultipartFile> files, User user) {
       Complaint complaint = complaintRepository.findById(complaintId).orElseThrow(()-> new ComplaintNotFoundException("Complaint not found"));
       if(!Objects.equals(complaint.getUser().getId(), user.getId())){
           throw new UnauthorizedActionException("Sorry ! you are not authorized user to take this action");
       }
       this.addAttachments(complaint, files);
    }







    /*TODO: from here the update work is started that can be update complaint or update complaintStatus,
    TODO: but specifically update tasks and Create a complaint task is done */

    @Transactional
    @Override
    public void partialUpdateComplaint(ComplaintRequest request, User currentUser) {
        Complaint complaint=complaintRepository.findByUserAndId(request.getId(), currentUser).orElseThrow(()-> new ComplaintNotFoundException("Complaint not found"));

        // Business Rule: Agar status PENDING nahi hai, toh user edit nahi kar sakta
        if (complaint.getStatus() != ComplaintStatus.PENDING) {
            throw new IllegalStateException("Cannot edit complaint once it is processed by admin");
        }
        if(request.getTitle() != null)complaint.setTitle(request.getTitle());
        if(request.getDescription() != null)complaint.setDescription(request.getDescription());
        if(request.getRemark() != null)complaint.setRemark(request.getRemark());
        if(request.getCategory() != null)complaint.setCategory(request.getCategory());
        if(request.getPriority() != null)complaint.setPriority(request.getPriority());
        complaint.setUpdatedAt(LocalDateTime.now());
        complaint.setUpdatedBy(currentUser);
    }
    @Transactional
    @Override
    public void updateComplaint(ComplaintUpdateRequest request, User user){
        Complaint complaint = complaintRepository.findById(request.getId()).orElseThrow(()-> new ComplaintNotFoundException("Complaint not found"));
        if(!Objects.equals(complaint.getUser().getId(), user.getId())){
            throw new UnauthorizedActionException("Sorry ! You are not authorized to perform this action");
        }
        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());
        complaint.setRemark(request.getRemark());
        complaint.setPriority(request.getPriority());
        complaint.setCategory(request.getCategory());
        complaint.setUpdatedAt(LocalDateTime.now());
        complaint.setUpdatedBy(user);

    }

    @Transactional
    @Override
    public ComplaintResponseDTO updateComplaintStatus(Long complaintId, ComplaintStatus newStatus, String remark, User admin) {
       Complaint complaint =  complaintRepository.findById(complaintId).orElseThrow(()-> new ComplaintNotFoundException("complaint not found"));
       ComplaintStatus previousStatus = complaint.getStatus();
        // 2. Business Rule: Resolved ya Rejected complaint ko dobara nahi chhed sakte (Optional but Recommended)
        if(!isValidTransition(previousStatus, newStatus)){
            throw new IllegalStateException("Invalid newStatus transition: Cannot change newStatus from " + previousStatus + " to " + newStatus);
        }
       complaint.setStatus(newStatus);
       if(remark != null){
        complaint.setRemark(remark);
       }
       if(complaint.getAssignedTo() == null) {
           complaint.setAssignedTo(admin);
           String userMsg = "Good news! Your complaint #" + complaint.getTicketId() + " has been assigned to " + admin.getUserProfile().getFullName() + ". We're on it!";
          //this goes to user
           webSocketService.sendPrivateNotification(complaint.getUser().getEmail(), new WebSocketService.NotificationResponse("Ticket Assigned", userMsg, complaint.getTicketId()));
           //this goes to admin
           String adminMsg = "New Task: Complaint #" + complaint.getTicketId() + " is assigned to you. Check details and start working.";
           webSocketService.sendPrivateNotification(admin.getEmail(), new WebSocketService.NotificationResponse("Task Assigned", adminMsg, complaint.getTicketId()));
       }

       if(newStatus == ComplaintStatus.RESOLVED){
        complaint.setResolvedAt(LocalDateTime.now());
       }
       complaint.setStatus(ComplaintStatus.IN_PROGRESS);
       complaint.setUpdatedAt(LocalDateTime.now());
       complaint.setUpdatedBy(admin);
       ComplaintLog savedLog = this.saveLog(complaint, previousStatus, newStatus);
        ComplaintLogResponseDTO dto = mapToComplaintLogResponseDto(savedLog);
        // ye updated log broadcast karne ke liye
        webSocketService.sendUpdatedLog(dto);
        //ye user ko notify karne ke liye private endpoint

                String statusChangeMessage="Good news! Your complaint #" + complaint.getTicketId() + "  newStatus has been changed to " + newStatus + ". We're on it!";
        webSocketService.sendPrivateNotification(complaint.getUser().getEmail(), new WebSocketService.NotificationResponse("STATUS_UPDATED", statusChangeMessage, complaint.getTicketId()));
        return this.mapToComplaintResponseDto(complaint);
    }
//TODO: here update work is done


    //todo: here is a super admin or admin work start

    @Transactional
    @Override
    public ComplaintResponseDTO setRemarkToComplaint(Long complaintId, String remark, User adminAndSuperAdmin){
        Complaint complaint = complaintRepository.findById(complaintId).orElseThrow(() -> new ComplaintNotFoundException("Complaint not found"));
        complaint.setRemark(remark);
        complaint.setUpdatedBy(adminAndSuperAdmin);
        complaint.setUpdatedAt(LocalDateTime.now());
        ComplaintLog complaintLog = saveLog(complaint, complaint.getStatus(), complaint.getStatus());
      //it sendUpdatedLog will send to this complaint id
        webSocketService.sendUpdatedLog(this.mapToComplaintLogResponseDto(complaintLog));
        return this.mapToComplaintResponseDto(complaint);
    }

    @Transactional
    @Override
    public Complaint assignTo(Long complaintId, Long adminId, User superAdmin) {
        Complaint complaint = complaintRepository.findById(complaintId).orElseThrow(() -> new ComplaintNotFoundException("Complaint not found"));
        User newAdmin = authRepository.findById(adminId).orElseThrow(() -> new UserNotFoundException("User not found"));

        String userMsg;
        String ticketId = complaint.getTicketId();
        String newAdminName = newAdmin.getUserProfile().getFullName();

        if (complaint.getAssignedTo() != null) {
            // CASE: Re-assignment
            String oldAdminName = complaint.getAssignedTo().getUserProfile().getFullName();
            userMsg = "Update: Your complaint #" + ticketId + " has been re-assigned from [" + oldAdminName + "] to [" + newAdminName + "].";
        } else {
            // CASE: First time assignment
            userMsg = "Good news! Your complaint #" + ticketId + " has been assigned to [" + newAdminName + "]. We're on it!";
            // Optional: complaint.setStatus(ComplaintStatus.IN_PROGRESS);
        }

        // 1. Database Update
        complaint.setAssignedTo(newAdmin);
        complaint.setUpdatedAt(LocalDateTime.now()); // Timestamp update karna mat bhulna
        String historyRemark = "Complaint assigned to [" + newAdminName + "] by " + superAdmin.getUserProfile().getFullName();
        complaint.setRemark(historyRemark);
        // 2. Notify User (Single Notification)
        webSocketService.sendPrivateNotification(
                complaint.getUser().getEmail(),
                new WebSocketService.NotificationResponse("TICKET_ASSIGNED", userMsg, ticketId)
        );

        // 3. Notify the New Admin
        String adminMsg = "New Task: Complaint #" + ticketId + " is assigned to you. Check details and start working.";
        webSocketService.sendPrivateNotification(
                newAdmin.getEmail(),
                new WebSocketService.NotificationResponse("TASK_ASSIGNED", adminMsg, ticketId)
        );

        ComplaintLog complaintLog = saveLog(complaint, complaint.getStatus(), complaint.getStatus());
        webSocketService.sendUpdatedLog(mapToComplaintLogResponseDto(complaintLog));
        return complaint;
    }

    //todo:from here all getter definition starts that can be for a complaint or complaintLog



    @Override
    @Transactional(readOnly = true)
    public Page<ComplaintResponseDTO> getMyAssignedComplaint(int page, int size, User admin){
       Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
      return complaintRepository.findByAssignedTo(admin, pageable).map(this::mapToComplaintResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ComplaintResponseDTO> getMyComplaints(int page, int size, User user) {
        // "createdAt" wahi naam hona chahiye jo hamri Complaint entity mein hai
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
       Page<Complaint> complaintPage= complaintRepository.findByUser(user, pageable);

       if(complaintPage==null || complaintPage.isEmpty())return Page.empty();

       return complaintPage.map(this::mapToComplaintResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ComplaintResponseDTO> getAllComplaintsForSuperAdmin(int page, int size, ComplaintStatus status, ComplaintCategory category) {

           Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
           Page<Complaint> complaints;
           if(status !=null && category !=null){
               complaints = complaintRepository.findByStatusAndCategory(status, category, pageable);
        }else if(category !=null){
               complaints= complaintRepository.findByCategory(category, pageable);

        } else if (status !=null) {
               complaints= complaintRepository.findByStatus(status, pageable);
       }else{
           complaints = complaintRepository.findAll(pageable);
       }

        return complaints.map(this::mapToComplaintResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ComplaintLogResponseDTO> getComplaintHistory(int page, int size, Long complaintId, User user) {
        Complaint complaint = complaintRepository.findById(complaintId).orElseThrow(() -> new ComplaintNotFoundException("Complaint not found"));
        boolean isOwner = Objects.equals(complaint.getUser().getId(), user.getId());
        boolean isSuperAdmin = complaint.getUser().getRoles().contains(UserRole.ROLE_SUPER_ADMIN);
        if(!isOwner && !isSuperAdmin){
            throw new UnauthorizedActionException("Sorry ! You are not authorized to perform this action");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("logTime").ascending());
        Page<ComplaintLog> complaintLog = complaintLogRepository.findAllByComplaint(complaint, pageable);
       if(complaintLog == null || complaintLog.isEmpty()){
           return Page.empty();
       }
      return complaintLog.map(this::mapToComplaintLogResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ComplaintResponseDTO getComplaintByTicketId(String ticketId, User currentUser) {
        // TicketId unique hai, toh seedha Optional/Single object nikalo
        Complaint complaint = complaintRepository.findByTicketId(ticketId)
                .orElseThrow(() -> new ComplaintNotFoundException("Invalid Ticket ID"));
        boolean isAdmin = currentUser.getRoles().contains(UserRole.ROLE_ADMIN);
        boolean isOwner = Objects.equals(complaint.getUser().getId(), currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new UnauthorizedActionException("Sorry ! You are not authorized to perform this action");
        }
        return mapToComplaintResponseDto(complaint);
    }


    @Override
    @Transactional(readOnly = true)
    public List<ComplaintLogResponseDTO> getLogsByTicketId(String ticketId, User user) {
        // TicketId se sare logs uthao, latest upar (descending)
        List<ComplaintLog> logs = complaintLogRepository.findByTicketIdOrderByLogTimeDesc(ticketId);
        Complaint complaint = logs.getFirst().getComplaint();
        if(logs.isEmpty())return new ArrayList<>();
        boolean isOwner = Objects.equals(complaint.getUser().getId(), user.getId());
        boolean isSuperAdminOrAdmin = complaint.getUser().getRoles().contains(UserRole.ROLE_ADMIN) || complaint.getUser().getRoles().contains(UserRole.ROLE_SUPER_ADMIN);

        if (!isOwner && !isSuperAdminOrAdmin) {
            throw new UnauthorizedActionException("Sorry ! You are not authorized to perform this action");
        }
        return logs.stream()
                .map(this::mapToComplaintLogResponseDto)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<ComplaintResponseDTO> getTodayUpdatesAssignedAdmin(User admin) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay(); // Aaj ka 00:00
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX); // Aaj ka 23:59:59

        List<Complaint> updates = complaintRepository.findByAssignedToAndUpdatedAtBetween(admin,startOfDay, endOfDay);
        // map to DTO and return
        if(updates == null)return new ArrayList<>();
        return updates.stream().map(this::mapToComplaintResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintResponseDTO> getTodayUpdatesForUser(User user){
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay(); // Aaj ka 00:00
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX); // Aaj ka 23:59:59

        List<Complaint> recentComplaint = complaintRepository.findByUserAndUpdatedAtBetween(user, startOfDay, endOfDay);
        if(recentComplaint == null)return new ArrayList<>();
        return recentComplaint.stream().map(this::mapToComplaintResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintResponseDTO> getTodayUpdates() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay(); // Aaj ka 00:00
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX); // Aaj ka 23:59:59

        List<Complaint> updates = complaintRepository.findByUpdatedAtBetween(startOfDay, endOfDay);
        // map to DTO and return
        if(updates == null)return new ArrayList<>();
        return updates.stream().map(this::mapToComplaintResponseDto).toList();
    }

    //TODO: here is the last step of deleting the complaint when you delete the complaint it will delete the log as well

    @Transactional
    @Override
    public void deleteComplaint(Long complaintId, User currentUser) {
        Complaint complaint = complaintRepository.findById(complaintId).orElseThrow(()-> new ComplaintNotFoundException("Complaint not found"));
        // 1. Logic: Super Admin sab kuch uda sakta hai, User sirf apna.
        boolean isSuperAdmin = currentUser.getRoles().contains(UserRole.ROLE_SUPER_ADMIN);
        boolean isOwner = Objects.equals(complaint.getUser().getId(), currentUser.getId());

        if (!isSuperAdmin && !isOwner) {
            throw new UnauthorizedActionException("Sorry ! You are not authorized to perform this action");
        }

        if(complaint.getComplaintAttachment() != null){
            //before deleting the complaint, delete all attachments from cloud
            complaint.getComplaintAttachment().forEach(attachment -> cloudinaryService.delete(attachment.getPublicId()));
        }
        //after that, we delete the complaint entity
        //after complaint deletion the related logs deleted as well
        complaintRepository.delete(complaint);
    }


    /**
     * TODO: These are helper method that are used in this whole work I have done on above
     * */


    private String generateTicketId(){
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));
        int random = new SecureRandom().nextInt(9000)+1000;
        return "CMS"+datePart+random;
    }
//    private ComplaintResponseDTO mapToDto(Complaint complaint){
//return null;
//    }

    private ComplaintLog saveLog(Complaint complaint,ComplaintStatus previousStatus, ComplaintStatus newStatus){
        ComplaintLog log = new ComplaintLog();
        if(previousStatus !=null) log.setPreviousStatus(previousStatus);
        log.setComplaint(complaint);
        log.setRemark((complaint.getRemark() !=null)? complaint.getRemark() : previousStatus + " Status changed to "+ newStatus);
        log.setTicketId(complaint.getTicketId());
        log.setActionBy(complaint.getUpdatedBy());
        log.setLogTime(LocalDateTime.now());
        if(newStatus != null) log.setNewStatus(newStatus);
        complaint.getComplaintLogs().add(log);
        return log;
    }
    //this is responsible to handle all Complaint attachment
    //it will remove all existing files and added new one
    private List<ComplaintAttachment> updateAttachments(Complaint complaint, List<MultipartFile> files){
        if (files == null || files.isEmpty()) return new ArrayList<>();

        // first, we remove all cloudinary files
        if (complaint.getComplaintAttachment() != null) {
            complaint.getComplaintAttachment().forEach(att -> cloudinaryService.delete(att.getPublicId()));

            // DB row clear after cloudinary (Requires orphanRemoval=true in Entity)
            complaint.getComplaintAttachment().clear();
        }

        // 3. Then new files uploaded to cloudinary

        List<ComplaintAttachment> newAttachments = files.stream().map(file -> {
            Map upload = cloudinaryService.upload(file);
            ComplaintAttachment complaintAttachment = new ComplaintAttachment();
            complaintAttachment.setComplaint(complaint);
            complaintAttachment.setAttachmentUrls(upload.get("secure_url").toString());
            complaintAttachment.setPublicId(upload.get("public_id").toString());
            return complaintAttachment;
        }).toList();

        complaint.getComplaintAttachment().addAll(newAttachments);
        return newAttachments;
    }
    public void addAttachments (Complaint complaint, List<MultipartFile> files){
        if(files.isEmpty())return;
        files.forEach(file -> {
            Map upload = cloudinaryService.upload(file);
            ComplaintAttachment attachment = new ComplaintAttachment();
            attachment.setComplaint(complaint);
            attachment.setAttachmentUrls(upload.get("secure_url").toString());
            attachment.setPublicId(upload.get("public_id").toString());
            complaint.getComplaintAttachment().add(attachment);
        });
    }

    @Override
    public Map<String, Long> getAdminStats(Long adminId) {
        List<Object[]> objects = complaintRepository.countComplaintsByStatusForAdmin(adminId);
        Map<String, Long> report = new HashMap<>();
        long total =0;
        // Default values (taki frontend par 0 dikhe agar data na ho)
        report.put("PENDING", 0L);
        report.put("IN_PROGRESS", 0L);
        report.put("RESOLVED", 0L);
        for (Object[] results: objects){
            String status = results[0].toString();
            long count = (long) results[1];
            total += count;
            report.put(status, count);
        }
        report.put("TOTAL_WORKLOAD", total);
        return report;
    }

    @Override
    public Map<String, Long> getUserStats(Long userId) {
        List<Object[]> objects = complaintRepository.countComplaintsByStatusForUser(userId);
        Map<String, Long> report = new HashMap<>();
        long total =0;
        // Default values (taki frontend par 0 dikhe agar data na ho)
        report.put("PENDING", 0L);
        report.put("IN_PROGRESS", 0L);
        report.put("RESOLVED", 0L);
        for (Object[] results: objects){
            String status = results[0].toString();
            long count = (long) results[1];
            total += count;
            report.put(status, count);
        }
        report.put("TOTAL_Complaints", total);
        return report;
    }



    private ComplaintLogResponseDTO mapToComplaintLogResponseDto(ComplaintLog complaintLog){
       ComplaintLogResponseDTO responseDTO = new ComplaintLogResponseDTO();
       responseDTO.setComplaintId(complaintLog.getComplaint().getId());
       responseDTO.setActionBy(complaintLog.getActionBy().getUserProfile().getFullName());
       responseDTO.setId(complaintLog.getId());
       responseDTO.setRemark(complaintLog.getRemark());
       responseDTO.setNewStatus(complaintLog.getNewStatus());
       responseDTO.setPreviousStatus(complaintLog.getPreviousStatus());
       responseDTO.setLogTime(complaintLog.getLogTime());
       responseDTO.setTicketId(complaintLog.getTicketId());
       return responseDTO;

    }

    private ComplaintResponseDTO mapToComplaintResponseDto(Complaint complaint){
       ComplaintResponseDTO dto = new ComplaintResponseDTO();

       //direct fields that are not causing null
        //complaint info
        dto.setAttachments(complaint.getComplaintAttachment());
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


    private boolean isValidTransition(ComplaintStatus current, ComplaintStatus next){
       return switch (current){
           case PENDING -> (next == ComplaintStatus.IN_PROGRESS || next== ComplaintStatus.REJECTED);
           case IN_PROGRESS -> (next == ComplaintStatus.RESOLVED);
           case REJECTED, RESOLVED -> false; //this is a final state nothing happened or not changed
           default -> false;
       };
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
}
