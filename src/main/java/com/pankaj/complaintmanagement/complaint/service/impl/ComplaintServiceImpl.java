package com.pankaj.complaintmanagement.complaint.service.impl;

import com.pankaj.complaintmanagement.auth.repository.AuthRepository;
import com.pankaj.complaintmanagement.common.enums.ComplaintStatus;
import com.pankaj.complaintmanagement.complaint.dto.ComplaintLogDTO;
import com.pankaj.complaintmanagement.complaint.dto.ComplaintRequest;
import com.pankaj.complaintmanagement.complaint.dto.ComplaintResponseDTO;
import com.pankaj.complaintmanagement.complaint.repository.ComplaintLogRepository;
import com.pankaj.complaintmanagement.complaint.repository.ComplaintRepository;
import com.pankaj.complaintmanagement.complaint.service.ComplaintService;
import com.pankaj.complaintmanagement.entity.Complaint;
import com.pankaj.complaintmanagement.entity.ComplaintLog;
import com.pankaj.complaintmanagement.entity.User;
import com.pankaj.complaintmanagement.entity.UserProfile;
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

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
@Service
public class ComplaintServiceImpl implements ComplaintService {
   private final AuthRepository authRepository;
   private final ComplaintRepository complaintRepository;
   private final ComplaintLogRepository complaintLogRepository;
   private final UserProfileRepository userProfileRepository;
   @Autowired
    public ComplaintServiceImpl(AuthRepository authRepository, ComplaintRepository complaintRepository, ComplaintLogRepository complaintLogRepository, UserProfileRepository userProfileRepository) {
        this.authRepository = authRepository;
        this.complaintRepository = complaintRepository;
        this.complaintLogRepository = complaintLogRepository;
        this.userProfileRepository = userProfileRepository;
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
        complaint.setPreviousStatus(ComplaintStatus.PENDING);
        complaint.setCreatedAt(LocalDateTime.now());
        complaint.setActionBy(profile.getFullName());
        complaint.setTicketId(ticketId);
        complaint.setUser(foundUser);


        ComplaintLog firstLog = new ComplaintLog();
        firstLog.setComplaintStatus(ComplaintStatus.PENDING);
        firstLog.setComplaint(complaint);
        firstLog.setRemark(complaint.getRemark());
        firstLog.setTicketId(complaint.getTicketId());
        firstLog.setActionBy(complaint.getActionBy());
        firstLog.setLogTime(LocalDateTime.now());
        complaint.getComplaintLogs().add(firstLog);
        complaintRepository.save(complaint);

    }
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    @Override
    public void updateComplaintStatus(Long complaintId, ComplaintStatus status, String remark, User admin) {
       User foundUser = authRepository.findById(admin.getId()).orElseThrow(()-> new UserNotFoundException("user not found"));
       UserProfile profile = userProfileRepository.findByUser(foundUser).orElseThrow(()-> new UserProfileNotFoundException("user profile not found"));
    Complaint complaint =  complaintRepository.findById(complaintId).orElseThrow(()-> new ComplaintNotFoundException("complaint not found"));
    complaint.setPreviousStatus(status);
    complaint.setRemark((remark != null) ? remark : "Status changed to "+ status);
    complaint.setUpdatedAt(LocalDateTime.now());
    ComplaintLog log = new ComplaintLog();
    log.setComplaint(complaint);
    log.setComplaintStatus(complaint.getPreviousStatus());
    log.setTicketId(complaint.getTicketId());
    log.setRemark(complaint.getRemark());
    log.setActionBy(profile.getFullName());
    log.setLogTime(LocalDateTime.now());
    complaint.getComplaintLogs().add(log);
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

    }
}
